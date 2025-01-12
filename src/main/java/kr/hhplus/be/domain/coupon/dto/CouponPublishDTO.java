package kr.hhplus.be.domain.coupon.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponPublishDTO {
    private Long couponId;
    private Long userId;
    LocalDate validStartDate;
    LocalDate validEndDate;
}
