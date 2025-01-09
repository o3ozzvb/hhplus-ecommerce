package kr.hhplus.be.domain.user.dto;

import kr.hhplus.be.domain.coupon.enumtype.CouponPublishStatus;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
public class UserCouponDTO {

    private long couponPublishId;

    private long couponId;

    private String couponName;

    private DiscountType discountType;

    private int discountValue;

    private LocalDate publishDate;

    private LocalDate validStartDate;

    private LocalDate validEndDate;

    private CouponPublishStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
