package kr.hhplus.be.domain.coupon.service;

import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.support.exception.BusinessException;
import kr.hhplus.be.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponPublishRepository couponPublishRepository;

    /**
     * 쿠폰 발급
     */
    @Override
    public CouponPublish publishCoupon(CouponPublishDTO publishDTO) {
        Coupon coupon = couponRepository.findById(publishDTO.getCouponId());
        // 잔여수량이 없으면
        if (coupon.getRemainQuantity() <= 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_COUPON_QUANTITY);
        }

        return couponPublishRepository.save(CouponPublish.publishNow(publishDTO));
    }

    /**
     * 쿠폰 사용 처리
     */
    @Override
    public void redeemCoupon(long couponPublishId) {
        CouponPublish publishedCoupon = couponPublishRepository.findById(couponPublishId);
        publishedCoupon.redeem();
        couponPublishRepository.save(publishedCoupon);
    }

    /**
     * 할인 금액 계산
     */
    @Override
    public int getDiscountAmount(long couponPublishId, int totalAmount) {
        int discountAmount = 0;
        CouponPublish publishedCoupon = couponPublishRepository.findById(couponPublishId);
        Coupon coupon = couponRepository.findById(publishedCoupon.getRefCouponId());
        if (coupon.getDiscountType().equals(DiscountType.FIXED_RATE)) {
            discountAmount = totalAmount * coupon.getDiscountValue() / 100;
        }
        if (coupon.getDiscountType().equals(DiscountType.FIXED_AMOUNT)) {
            discountAmount = Math.min(totalAmount, coupon.getDiscountValue());
        }
        return discountAmount;
    }
}
