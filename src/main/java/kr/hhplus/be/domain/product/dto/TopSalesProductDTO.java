package kr.hhplus.be.domain.product.dto;

import kr.hhplus.be.domain.product.enumtype.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopSalesProductDTO {
    private long productId;
    private String productName;
    private Category category;
    private int price;
    private int soldQuantity;
    private int totalAmount;
}
