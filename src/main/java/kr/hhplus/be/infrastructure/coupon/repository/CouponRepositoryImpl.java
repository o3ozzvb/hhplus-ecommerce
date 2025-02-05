package kr.hhplus.be.infrastructure.coupon.repository;

import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.infrastructure.coupon.jpa.CouponJpaRepository;
import kr.hhplus.be.domain.exception.CommerceNotFoundException;
import kr.hhplus.be.domain.exception.ErrorCode;
import kr.hhplus.be.infrastructure.coupon.redis.CouponRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponRedisRepository couponRedisRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Coupon findById(long id) {
        return couponJpaRepository.findById(id)
                .orElseThrow(() -> new CommerceNotFoundException(ErrorCode.COUPON_NOT_EXIST));
    }

    @Override
    public Coupon findByIdForUpdate(Long couponId) {
        return couponJpaRepository.findByIdForUpdate(couponId)
                .orElseThrow(() -> new CommerceNotFoundException(ErrorCode.COUPON_NOT_EXIST));
    }

    @Override
    public void deleteAll() {
        couponJpaRepository.deleteAll();
    }

    @Override
    public void decreaseRemainQuantity(Long couponId) {
        couponJpaRepository.decreaseRemainQuantity(couponId);
    }

    @Override
    public void cacheCouponQuantity(long couponId, int quantity) {
        couponRedisRepository.cacheCouponQuantity(couponId, quantity);
    }

    @Override
    public Integer getCacheRemainQuantity(long couponId) {
        return couponRedisRepository.getCouponRemainQuantity(couponId);
    }

    @Override
    public void decreaseCacheRemainQuantity(Long couponId) {
        couponRedisRepository.decreateRemainQuantity(couponId);
    }
}
