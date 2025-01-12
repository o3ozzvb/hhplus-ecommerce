package kr.hhplus.be.domain.product.dto;

import kr.hhplus.be.domain.product.enumtype.Category;
import lombok.Getter;

@Getter
public class TopSalesProductDTO {
    private long rank;
    private long productId;
    private Category category;
    private String productName;
    private int price;
    private int soldQuantity;
    private int totalAmount;
}
