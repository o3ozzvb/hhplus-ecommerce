package kr.hhplus.be.domain.coupon.service;


import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;

public interface CouponService {
    /**
     * 쿠폰 발급
     */
    CouponPublish publishCoupon(CouponPublishDTO publishDTO);

    /**
     * 쿠폰 사용 처리
     */
    void redeemCoupon(long couponPublishId);

    /**
     * 할인 금액 계산
     */
    int getDiscountAmount(long couponPublishId, int totalAmount);
}
