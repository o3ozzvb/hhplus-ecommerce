package kr.hhplus.be.interfaces.dto.product;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TopSalesProducts {
    List<TopSalesProduct> topSalesProducts;
}
