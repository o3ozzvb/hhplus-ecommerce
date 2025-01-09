package kr.hhplus.be.fixture;

import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.enumtype.CouponPublishStatus;
import kr.hhplus.be.domain.coupon.enumtype.CouponStatus;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CouponFixture {

    public static Coupon createCoupon(String name, DiscountType discountType, int discountValue, int remainQuantiy) {
        return new Coupon(null, name, discountType, discountValue, 30, remainQuantiy, CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
    }

    public static CouponPublish createCouponPublish(long couponId, long userId) {
        return new CouponPublish(null, couponId, userId, LocalDate.now(), null, LocalDate.now(), LocalDate.now().plusDays(30), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());
    }
}
