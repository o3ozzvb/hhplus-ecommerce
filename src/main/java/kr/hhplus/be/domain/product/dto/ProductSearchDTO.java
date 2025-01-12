package kr.hhplus.be.domain.product.dto;

import kr.hhplus.be.domain.product.enumtype.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductSearchDTO {
    private String productName;
    private int startPrice;
    private int endPrice;
    private Category category;
}
