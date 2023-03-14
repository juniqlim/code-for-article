package juniq.article.oop;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
class A {
    private Long a1;
    private String a2;
    private String a3;
    private BigDecimal a4;
}
