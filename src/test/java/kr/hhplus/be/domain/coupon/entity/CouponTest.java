package kr.hhplus.be.domain.coupon.entity;

import kr.hhplus.be.domain.coupon.enumtype.CouponStatus;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.exception.CommerceConflictException;
import kr.hhplus.be.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @Test
    @DisplayName("쿠폰발급 시 잔여수량이 0이면 CommerceConflictException이 발생한다.")
    void publish(){
        // given
        Coupon coupon = new Coupon(null, "쿠폰", DiscountType.FIXED_RATE, 10, 30, 0, CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        // when

        // then
        assertThatThrownBy(() -> coupon.publish())
                .isInstanceOf(CommerceConflictException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getMessage());
    }


}