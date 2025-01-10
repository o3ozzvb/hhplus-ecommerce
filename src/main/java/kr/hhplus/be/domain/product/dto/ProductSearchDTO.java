package kr.hhplus.be.domain.product.dto;

import kr.hhplus.be.domain.product.enumtype.Category;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSearchDTO {
    private String productName;
    private Integer startPrice;
    private Integer endPrice;
    private Category category;
}
