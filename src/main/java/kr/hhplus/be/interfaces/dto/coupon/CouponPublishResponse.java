package kr.hhplus.be.interfaces.dto.coupon;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponPublishResponse {
    private long couponPublishId;
}
