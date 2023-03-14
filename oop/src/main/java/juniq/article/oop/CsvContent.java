package juniq.article.oop;

import static java.util.Comparator.comparing;
import static java.util.stream.Nodes.collect;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CsvContent {
    private final String accountNumber;
    private final LocalDate statementDate;
    private final String beginningBalance;
    private final String endingBalance;
    private final List<B> bList;

    public CsvContent(String accountNumber, BigDecimal beginningBalance, List<B> bList) {
        this(accountNumber, LocalDate.now(), beginningBalance, bList);
    }

    public CsvContent(String accountNumber, LocalDate statementDate, BigDecimal beginningBalance, List<B> bList) {
        if (bList.isEmpty()) {
            this.accountNumber = accountNumber;
            this.statementDate = statementDate;
            this.bList = Collections.emptyList();
            this.beginningBalance = beginningBalance.toString();
            this.endingBalance = this.beginningBalance;
        } else {
            this.accountNumber = accountNumber;
            this.statementDate = statementDate;
            this.bList = bList.stream().sorted(comparing(B::getB4())).collect(Collectors.toList());
            this.beginningBalance = calculateBeginningBalance(bList).toString();
            this.endingBalance = bList.get(bList.size() - 1)
                    .getB3()
                    .orElse(BigDecimal.ZERO)
                    .toString();
        }
    }

    private static BigDecimal calculateBeginningBalance(List<B> histories) {
        B firstHistory = histories.get(0);
        BigDecimal beginningBalance = firstHistory.getB3().orElse(BigDecimal.ZERO);

        if (isCredit(firstHistory)) {
            return beginningBalance.subtract(firstHistory.getFundAmount());
        }

        if (isDebit(firstHistory)) {
            return beginningBalance.add(firstHistory.getFundAmount());
        }

        return beginningBalance;
    }

    private static boolean isCredit(B firstHistory) {
        String fundType = firstHistory.getB1();
        return "credit".equalsIgnoreCase(fundType);
    }

    private static boolean isDebit(B firstHistory) {
        String fundType = firstHistory.getB1();
        return "debit".equalsIgnoreCase(fundType);
    }

    public List<String[]> getContent() {
        List<String[]> content = new ArrayList<>();

        content.add(new String[]{"Account Number", accountNumber});
        content.add(new String[]{"Statement Date", statementDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))});
        content.add(new String[]{"Beginning Balance", beginningBalance});
        content.add(new String[]{"Ending Balance", endingBalance});
        content.add(new String[]{"Transaction Type", "Transaction Date", "Transaction ID", "Description", "Currency", "Amount"});
        for (B history : bList) {
            content.add(history.getContent());
        }

        return content;
    }
}