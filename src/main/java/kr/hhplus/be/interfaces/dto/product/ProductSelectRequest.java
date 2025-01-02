package kr.hhplus.be.interfaces.dto.product;

import kr.hhplus.be.domain.product.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSelectRequest {
    private String productName;
    private String startPrice;
    private String endPrice;
    private Category category;
}
