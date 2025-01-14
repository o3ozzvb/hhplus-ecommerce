package kr.hhplus.be.infrastructure.coupon.repository;

import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.infrastructure.coupon.jpa.CouponJpaRepository;
import kr.hhplus.be.support.exception.CommerceNotFoundException;
import kr.hhplus.be.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

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
}
