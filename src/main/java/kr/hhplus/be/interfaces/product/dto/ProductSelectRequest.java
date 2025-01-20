package kr.hhplus.be.interfaces.product.dto;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.domain.product.dto.ProductSearchDTO;
import kr.hhplus.be.domain.product.enumtype.Category;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSelectRequest {
    private String productName;
    private int startPrice;
    private int endPrice;
    private Category category;

    // paging info
    @NotNull
    private int page;
    @NotNull
    private int size;

    public ProductSearchDTO toDTO() {
        return ProductSearchDTO.builder()
                .productName(this.productName)
                .startPrice(this.startPrice)
                .endPrice(this.endPrice)
                .category(this.category).build();
    }
}
