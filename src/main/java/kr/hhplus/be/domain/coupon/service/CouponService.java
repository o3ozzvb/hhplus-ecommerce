package kr.hhplus.be.domain.coupon.service;


import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.exception.CommerceConflictException;
import kr.hhplus.be.domain.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponPublishRepository couponPublishRepository;

    /**
     * 쿠폰 발급
     */
    //@DistributedLock(value = "#publishDTO.couponId") -> 레디스 대기열 사용으로 분산락 제거
    @Transactional
    public CouponPublish publishCoupon(CouponPublishDTO publishDTO) {
        Coupon coupon = couponRepository.findById(publishDTO.getCouponId());
        // 쿠폰 발행
        coupon.publish();
        couponRepository.save(coupon);
        // 쿠폰 발행 내역 저장
        CouponPublish couponPublish = couponPublishRepository.save(CouponPublish.publishNow(publishDTO));
        return couponPublish;
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

    /**
     * 쿠폰 발급 요청 시 대기열에 요청 저장
     */
    @Transactional
    public void addCouponPublishRequest(CouponPublishDTO publishDTO) {
        // 잔여수량 검증
        validateRemainQuantity(publishDTO.getCouponId());
        // 중복 발급요청 검증
        validateDuplicateRequest(publishDTO);
        // set, sorted set 에 저장
        couponPublishRepository.savePublishRequest(publishDTO);
        // 잔여수량 차감
        decreaseRemainQuantity(publishDTO);
    }

    // 잔여수량 검증
    private void validateRemainQuantity(long couponId) {
        // 잔여 수량 검증
        Integer remainQuantity = couponRepository.getCacheRemainQuantity(couponId);
        // 현재 발급 요청 건 수 조회
        Integer couponPublishCount = couponPublishRepository.getCouponPublishCount(couponId);
        if (ObjectUtils.isEmpty(remainQuantity) || remainQuantity <= couponPublishCount) {
            throw new CommerceConflictException(ErrorCode.INSUFFICIENT_COUPON_QUANTITY);
        }
    }

    // 중복 발급요청 검증
    private void validateDuplicateRequest(CouponPublishDTO publishDTO) {
        if (couponPublishRepository.isCouponPublishRequested(publishDTO.getCouponId(), publishDTO.getUserId())) {
            log.info("validateDuplicateRequest TRUE !~ ");
            throw new CommerceConflictException(ErrorCode.ALREAY_REQUESTED_COUPON);
        }
    }

    // 잔여수량 차감
    private void decreaseRemainQuantity(CouponPublishDTO publishDTO) {
        couponRepository.decreaseRemainQuantity(publishDTO.getCouponId());
        couponRepository.decreaseCacheRemainQuantity(publishDTO.getCouponId());
    }

    /**
     * 쿠폰 발급 요청 목록 대기열에서 조회
     */
    public List<CouponPublishDTO> getCouponPublishRequests(long couponId, int requestCount) {
        return couponPublishRepository.getPublishRequest(couponId, requestCount);
    }

    /**
     * 쿠폰 마스터 저장
     */
    public Coupon saveCoupon(Coupon coupon) {
        Coupon savedCoupon = couponRepository.save(coupon);
        couponRepository.cacheCouponQuantity(savedCoupon.getId(), savedCoupon.getRemainQuantity());
        return savedCoupon;
    }
}
