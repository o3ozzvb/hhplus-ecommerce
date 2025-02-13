package kr.hhplus.be.domain.coupon.repository;

import kr.hhplus.be.domain.coupon.entity.Coupon;

public interface CouponRepository {
    Coupon save(Coupon coupon);

    Coupon findById(long id);

    Coupon findByIdForUpdate(Long couponId);

    void deleteAll();

    void decreaseRemainQuantity(Long couponId);

    void cacheCouponQuantity(long couponId, int quantity);

    Integer getCacheRemainQuantity(long couponId);

    void decreaseCacheRemainQuantity(Long couponId);
}
