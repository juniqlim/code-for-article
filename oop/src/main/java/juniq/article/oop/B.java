package juniq.article.oop;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
class B {
    private String b1;
    private String b2;
    private BigDecimal b3;
    private LocalDateTime b4;

    public Optional<BigDecimal> getb3() {
        if (b3 == null) {
            return Optional.empty();
        }
        return Optional.of(b3);
    }

    String[] getContent() {
        return new String[0];
    }
}
