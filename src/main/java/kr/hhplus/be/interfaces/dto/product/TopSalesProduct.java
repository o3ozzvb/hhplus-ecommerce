package kr.hhplus.be.interfaces.dto.product;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TopSalesProduct {
    private int rank;
    private long productId;
    private String productName;
    private int soldQuantity;
    private int totalAmount;
}
