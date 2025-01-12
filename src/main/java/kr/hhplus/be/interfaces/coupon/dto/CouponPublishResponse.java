package kr.hhplus.be.interfaces.coupon.dto;

import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponPublishResponse {
    private long couponPublishId;

    public static CouponPublishResponse from(CouponPublish couponPublish) {
        return new CouponPublishResponse(couponPublish.getId());
    }
}
