package kr.hhplus.be.interfaces.dto.coupon;

import kr.hhplus.be.domain.coupon.DiscountType;
import kr.hhplus.be.domain.coupon.CouponPublishStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponInfo {
    private Long couponId;
    private String couponName;
    private LocalDate publishDate;
    private DiscountType discountType;
    private int discountValue;
    private LocalDate validStartDate;
    private LocalDate validEndDate;
    private CouponPublishStatus status;
}
