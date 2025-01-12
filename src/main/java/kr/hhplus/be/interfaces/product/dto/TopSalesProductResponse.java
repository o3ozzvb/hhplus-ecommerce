package kr.hhplus.be.interfaces.product.dto;

import kr.hhplus.be.domain.product.dto.TopSalesProductDTO;
import kr.hhplus.be.domain.product.enumtype.Category;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TopSalesProductResponse {
    private long productId;
    private Category category;
    private String productName;
    private int price;
    private int soldQuantity;
    private int totalAmount;

    public static TopSalesProductResponse from(TopSalesProductDTO productDTO) {
        TopSalesProductResponse response = new TopSalesProductResponse();

        response.productId = productDTO.getProductId();
        response.productName = productDTO.getProductName();
        response.category = productDTO.getCategory();
        response.price = productDTO.getPrice();
        response.soldQuantity = productDTO.getSoldQuantity();
        response.totalAmount = productDTO.getTotalAmount();

        return response;
    }
}
