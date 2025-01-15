package kr.hhplus.be.domain.coupon.service;

import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.enumtype.CouponPublishStatus;
import kr.hhplus.be.domain.coupon.enumtype.CouponStatus;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.exception.CommerceConflictException;
import kr.hhplus.be.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    CouponService couponService;

    @Mock
    CouponRepository couponRepository;

    @Mock
    CouponPublishRepository couponPublishRepository;

    @Test
    @DisplayName("쿠폰 발급 시 사용자ID로 쿠폰ID의 쿠폰이 발행된다.")
    void publishCoupon() {
        // given
        long couponPublishId = 1L;
        long couponId = 1L;
        long userId = 1L;

        Coupon coupon = new Coupon(couponId, "쿠폰", DiscountType.FIXED_RATE, 10, 30, 30, CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        CouponPublish couponPublish = new CouponPublish(couponPublishId, couponId, userId, LocalDate.now(), null, LocalDate.now(), LocalDate.now().plusDays(30), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());

        when(couponRepository.findByIdForUpdate(couponId)).thenReturn(coupon);
        when(couponPublishRepository.findById(couponPublishId)).thenReturn(couponPublish);
        when(couponPublishRepository.save(any(CouponPublish.class))).thenReturn(couponPublish);

        // when
        CouponPublishDTO publishDTO = CouponPublishDTO.builder()
                .couponId(couponId)
                .userId(userId)
                .validStartDate(LocalDate.now())
                .validEndDate(LocalDate.now().plusDays(30)).build();
        couponService.publishCoupon(publishDTO);

        // then
        CouponPublish findPublishedCoupon = couponPublishRepository.findById(couponPublishId);
        assertThat(findPublishedCoupon)
                .extracting("refCouponId", "refUserId")
                .containsExactly(couponId, userId);
    }

    @Test
    @DisplayName("쿠폰 발급 시 잔여발급수량이 없으면 CommerceConflictException이 발생한다.")
    void publishCoupon_exception() {
        // given
        long couponPublishId = 1L;
        long couponId = 1L;
        long userId = 1L;

        Coupon coupon = new Coupon(couponId, "쿠폰", DiscountType.FIXED_RATE, 10, 30, 0, CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        CouponPublish couponPublish = new CouponPublish(couponPublishId, couponId, userId, LocalDate.now(), null, LocalDate.now(), LocalDate.now().plusDays(30), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());

        when(couponRepository.findByIdForUpdate(couponId)).thenReturn(coupon);

        // when
        CouponPublishDTO publishDTO = CouponPublishDTO.builder()
                .couponId(couponId)
                .userId(userId)
                .validStartDate(LocalDate.now())
                .validEndDate(LocalDate.now().plusDays(30)).build();

        // then
        assertThatThrownBy(() -> couponService.publishCoupon(publishDTO))
                .isInstanceOf(CommerceConflictException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getMessage());
    }

    @Test
    @DisplayName("정률 할인쿠폰의 할인금액이 제대로 계산된다.")
    void getDiscountAmount() {
        // given
        int discountValue = 10;
        BigDecimal totalAmount = BigDecimal.valueOf(115000);
        LocalDate today = LocalDate.now();
        Coupon coupon = new Coupon(1L, "10% 할인 쿠폰", DiscountType.FIXED_RATE, discountValue, 30, 30, CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        CouponPublish couponPublish = new CouponPublish(1L, 1L, 1L, today, null, today, today.plusDays(30), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());

        when(couponRepository.findById(anyLong())).thenReturn(coupon);
        when(couponPublishRepository.findById(anyLong())).thenReturn(couponPublish);

        // when
        BigDecimal discountAmount = couponService.getDiscountAmount(couponPublish.getId(), totalAmount);

        // then
        assertThat(discountAmount).isEqualTo(totalAmount.multiply(BigDecimal.valueOf(discountValue).divide(BigDecimal.valueOf(100))));
    }

    @Test
    @DisplayName("정액 할인쿠폰의 할인금액이 제대로 계산된다.")
    void getDiscountAmount2() {
        // given
        int discountValue = 50000;
        BigDecimal totalAmount = BigDecimal.valueOf(115000);
        LocalDate today = LocalDate.now();
        Coupon coupon = new Coupon(1L, "50000원 할인 쿠폰", DiscountType.FIXED_AMOUNT, discountValue, 30, 30, CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        CouponPublish couponPublish = new CouponPublish(null, 1L, 1L, today, null, today, today.plusDays(30), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());

        when(couponRepository.findById(1L)).thenReturn(coupon);
        when(couponPublishRepository.findById(1L)).thenReturn(couponPublish);

        // when
        BigDecimal discountAmount = couponService.getDiscountAmount(coupon.getId(), totalAmount);

        // then
        assertThat(discountAmount).isEqualTo(BigDecimal.valueOf(discountValue));
    }
}