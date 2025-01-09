package kr.hhplus.be.domain.product.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    OUTER("아우터"),
    TOP("상의"),
    PANTS("하의"),
    ACCESSORIES("악세사리"),
    ETC("잡화");

    private String description;
}
