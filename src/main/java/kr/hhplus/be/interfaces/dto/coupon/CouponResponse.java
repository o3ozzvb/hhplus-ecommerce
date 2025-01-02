package kr.hhplus.be.interfaces.dto.coupon;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponResponse {
    private List<CouponInfo> couponInfoList;
}
