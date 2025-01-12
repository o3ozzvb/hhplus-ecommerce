package kr.hhplus.be.domain.coupon.entity;

import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.enumtype.CouponPublishStatus;
import kr.hhplus.be.support.exception.BusinessException;
import kr.hhplus.be.support.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponPublishTest {

    @Test
    @DisplayName("쿠폰 발행 시 발행일자, 상태가 오늘 날짜, AVAILABLE 로 생성된다.")
    void publishNow() {
        // given
        LocalDate today = LocalDate.now();
        CouponPublishDTO couponPublishDTO = CouponPublishDTO.builder()
                .couponId(1L)
                .userId(1L)
                .validStartDate(today)
                .validEndDate(today.plusDays(30))
                .build();

        // when
        CouponPublish couponPublish = CouponPublish.publishNow(couponPublishDTO);

        // then
        assertThat(couponPublish.getPublishDate()).isEqualTo(today);
        assertThat(couponPublish.getStatus()).isEqualTo(CouponPublishStatus.AVAILABLE);
    }

    @Test
    @DisplayName("쿠폰 사용 시 사용일자, 상태가 오늘 날짜, REDEEMED 로 업데이트된다.")
    void redeem() {
        // given
        LocalDate today = LocalDate.now();
        CouponPublish couponPublish = new CouponPublish(null, 1L, 1L, today, null, today, today.plusDays(30), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());

        // when
        couponPublish.redeem();

        // then
        assertThat(couponPublish.getRedeemDate()).isEqualTo(today);
        assertThat(couponPublish.getStatus()).isEqualTo(CouponPublishStatus.REDEEMED);
    }

    @Test
    @DisplayName("쿠폰 사용 시 쿠폰 상태가 AVAILABLE이 아니라면 BusinessException이 발생한다.")
    void redeem_exception() {
        // given
        LocalDate today = LocalDate.now();
        CouponPublish couponPublish = new CouponPublish(null, 1L, 1L, today, null, today, today.plusDays(30), CouponPublishStatus.REDEEMED, LocalDateTime.now(), LocalDateTime.now());

        // when

        // then
        assertThatThrownBy(() -> couponPublish.redeem())
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COUPON_NOT_AVAILABLE.getMessage());
    }

    @Test
    @DisplayName("쿠폰 사용 시 쿠폰 상태가 AVAILABLE이 아니라면 BusinessException이 발생한다.")
    void redeem_exception2() {
        // given
        LocalDate today = LocalDate.now();
        CouponPublish couponPublish = new CouponPublish(null, 1L, 1L, today, null, LocalDate.of(2024,12,01), LocalDate.of(2024,12,31), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());

        // when

        // then
        assertThatThrownBy(() -> couponPublish.redeem())
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COUPON_VALID_DATE_EXPIRED.getMessage());
    }
}