package juniq.article.oop;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class Service {
    private final JdbcTemplate jdbcTemplate;
    private static Logger log = LoggerFactory.getLogger(Service.class);

    public Service(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void ftpUploadCsvFile(String date) {
        try {
            ARowMapper aRowMapper = new ARowMapper();
            List<A> aList = jdbcTemplate.query(getPartnersQuery(), aRowMapper);
            for (A a : aList) {
                BRowMapper historyRowMapper = new BRowMapper();
                List<B> bList = jdbcTemplate.query(getInboundFundHistoryQuery(date, a), historyRowMapper);
                CsvContent csvContent = new CsvContent(a.getA1(), a.getA2(), bList);
                uploadToFileServer(csvContent, getDate(date), a);
            }
            return RepeatStatus.FINISHED;
        } catch (Exception e) {
            log.error(e.getMessage());
            contribution.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
        }
    }

    private String getPartnersQuery() {
        return "SELECT IB_PARTNER_SEQ, PARTNER_CODE, BALANCE_CURRENCY, BALANCE\n" +
            "FROM IB_PARTNER\n" +
            "WHERE DELETED = 0";
    }

    private String getInboundFundHistoryQuery(String date, A partner) {
        date = getDate(date);
        String between = String.format("'%sT00:00:00.00' AND '%sT23:59:59.999'", date, date);
        return String.format("SELECT CASE\n" +
            "           WHEN FUND_TYPE = 'USE' THEN 'DEBIT'\n" +
            "           WHEN FUND_TYPE = 'FUND' THEN 'CREDIT'\n" +
            "           WHEN FUND_TYPE = 'CREDIT' THEN 'CREDIT'\n" +
            "           ELSE 'KNOWN TYPE' END AS FUND_TYPE,\n" +
            "       REG_DATE,\n" +
            "       [IB_REMITTANCE_SEQ]       AS TRANSACTION_ID,\n" +
            "       [DESCRIPTION],\n" +
            "       FUND_CURRENCY,\n" +
            "       FUND_AMOUNT,\n" +
            "       BALANCE\n" +
            "FROM IB_PARTNER_FUND_HISTORY\n" +
            "WHERE IB_PARTNER_SEQ = %s\n" +
            "  AND REG_DATE BETWEEN %s\n" +
            "ORDER BY REG_DATE", partner.getIbPartnerSeq(), between);
    }

    private String getDate(String date) {
        if (date == null || date.isEmpty()) {
            return LocalDate.now().format(ofPattern("yyyy-MM-dd"));
        }

        // verify
        LocalDate.parse(date, ofPattern("yyyy-MM-dd"));

        return date;
    }

    private void uploadToFileServer(CsvContent csvContent, String date, A partner) throws IOException {
        File file = File.createTempFile(UUID.randomUUID().toString(), ".tmp"); // create a temporary file for writing data
        file.deleteOnExit(); // when the upload is complete, the temporary file is removed.

        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);
        writer.writeAll(csvContent.getContent());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file));) {
            bw.write(sw.toString());
            String targetPath = date + "/" + partner.getPartnerCode() + ".csv";
            log.info("upload target path = {}", targetPath);
            sftpClient().upload(targetPath, file);
        } catch (IOException | JSchException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Value("${settle.inbound.partner.ip}")
    private String host;
    @Value("${spring.profiles.active}")
    private String profiles;

    private DefaultSftpClient sftpClient() {
        return new DefaultSftpClient(
            SftpProperties.builder()
                .keyMode(true)
                .host(host)
                .username(IbPartnerServerProperties.USERNAME)
                .privateKey(IbPartnerServerProperties.PRIVATE_KEY_PATH)
                .root(IbPartnerServerProperties.ROOT_PATH + profiles + "/")
                .build()
        );
    }

    private static class IbPartnerServerProperties {
        private static final String USERNAME = "sftpuser";
        private static final String PRIVATE_KEY_PATH = Paths.get(System.getProperty("user.home"), ".ssh", "id_rsa").toString();
        private static final String ROOT_PATH = "/Log_Drive/INBOUND_CALCULATION/";
    }

    public class ARowMapper implements RowMapper<A> {
        @Override
        public A mapRow(ResultSet rs, int rowNum) throws SQLException {
            return A.builder()
                .a1(rs.getLong("A1"))
                .a2(rs.getString("A2"))
                .a3(rs.getString("A3"))
                .a4(rs.getBigDecimal("A4"))
                .build();
        }
    }

    public class BRowMapper implements RowMapper<B> {
        @Override
        public B mapRow(ResultSet rs, int rowNum) throws SQLException {
            return B.builder()
                .b1(rs.getString("B1"))
                .b2(rs.getString("B2"))
                .b3(rs.getString("B3"))
                .b4(rs.getTimestamp("B4").toLocalDateTime())
                .build();
        }
    }
}