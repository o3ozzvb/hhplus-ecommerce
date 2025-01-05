package kr.hhplus.be.interfaces.dto.product;

import kr.hhplus.be.domain.product.Category;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductInfo {
    private long id;
    private String productName;
    private Category category;
    private int inventory;
    private int price;
}
