package kr.hhplus.be.domain.coupon.repository;

import kr.hhplus.be.domain.coupon.entity.Coupon;

public interface CouponRepository {
    Coupon save(Coupon coupon);

    Coupon findById(long id);

    Coupon findByIdForUpdate(Long couponId);
}
