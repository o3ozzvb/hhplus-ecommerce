package kr.hhplus.be.domain.coupon.dto;

import kr.hhplus.be.domain.coupon.enumtype.CouponPublishStatus;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Builder
@ToString
public class CouponSearchDTO {
    private Long userId;
    private String couponName;
    private DiscountType discountType;
    private LocalDate startDate;
    private LocalDate endDate;
    private CouponPublishStatus status;
}
