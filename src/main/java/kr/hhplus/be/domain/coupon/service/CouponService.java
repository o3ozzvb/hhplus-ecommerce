package kr.hhplus.be.domain.coupon.service;


import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.infrastructure.redisson.RedissonLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponPublishRepository couponPublishRepository;

    /**
     * 쿠폰 발급
     */
    @RedissonLock(key = "couponLock")
    @Transactional
    public CouponPublish publishCoupon(CouponPublishDTO publishDTO) {
        Coupon coupon = couponRepository.findByIdForUpdate(publishDTO.getCouponId());
        // 쿠폰 발행 - 잔여수량 차감
        coupon.publish();
        couponRepository.save(coupon);
        // 쿠폰 발행 내역 저장
        return couponPublishRepository.save(CouponPublish.publishNow(publishDTO));
    }

    /**
     * 쿠폰 사용 처리
     */
    public void redeemCoupon(long couponPublishId) {
        CouponPublish publishedCoupon = couponPublishRepository.findById(couponPublishId);
        publishedCoupon.redeem();
        couponPublishRepository.save(publishedCoupon);
    }

    /**
     * 할인 금액 계산
     */
    public BigDecimal getDiscountAmount(long couponPublishId, BigDecimal totalAmount) {
        BigDecimal discountAmount = BigDecimal.ZERO;
        CouponPublish publishedCoupon = couponPublishRepository.findById(couponPublishId);
        Coupon coupon = couponRepository.findById(publishedCoupon.getRefCouponId());
        if (coupon.getDiscountType().equals(DiscountType.FIXED_RATE)) {
            discountAmount = totalAmount.multiply(BigDecimal.valueOf(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100)));
        }
        if (coupon.getDiscountType().equals(DiscountType.FIXED_AMOUNT)) {
            discountAmount = totalAmount.min(BigDecimal.valueOf(coupon.getDiscountValue()));
        }
        return discountAmount;
    }
}
