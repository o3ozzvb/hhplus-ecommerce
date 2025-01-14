package kr.hhplus.be.interfaces.product.dto;

import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.enumtype.Category;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductResponse {
    private long id;
    private String productName;
    private Category category;
    private BigDecimal price;

    public static ProductResponse from(Product product) {
        ProductResponse response = new ProductResponse();

        response.id = product.getId();
        response.productName = product.getProductName();
        response.category = product.getCategory();
        response.price = product.getPrice();

        return response;
    }
}