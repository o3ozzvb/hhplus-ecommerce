package kr.hhplus.be.application.coupon;

import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.exception.CommerceConflictException;
import kr.hhplus.be.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class CouponFacadeIntegrationTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponPublishRepository couponPublishRepository;

    @Test
    @DisplayName("레디스에서 쿠폰발급 요청 N건을 가져와 발급요청하면 쿠폰이 발급된다")
    void publishPendingRequestsTest() {
        // given
        Coupon coupon = couponRepository.save(Coupon.of("선착순쿠폰", DiscountType.FIXED_AMOUNT, 10000, 10));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        // 쿠폰 발급 요청 10건 (레디스에 저장)
        for (int i=0; i<10; i++) {
            couponPublishRepository.savePublishRequest(new CouponPublishDTO(coupon.getId(), Long.valueOf(i), startDate, endDate));
        }

        // when
        // 레디스에 3건 가져와 발급 요청
        couponFacade.publishPendingRequests(coupon.getId(), 3);

        // then
        List<CouponPublish> publishedCoupons = couponPublishRepository.findAllByRefCouponId(coupon.getId());
        assertThat(publishedCoupons).hasSize(3) // 3건의 요청 건이 발급됨
                .extracting("refCouponId")
                .containsExactly(coupon.getId(), coupon.getId(), coupon.getId());
    }

    @Test
    @DisplayName("잔여수량 3건인 쿠폰을 레디스에서 쿠폰발급 요청 5건을 가져와 발급요청하면 CommerceConflictException이 발생하고 3건은 발급에 성공한다")
    void publishPendingRequestsExceptionTest() {
        // given
        Coupon coupon = couponRepository.save(Coupon.of("선착순쿠폰", DiscountType.FIXED_AMOUNT, 10000, 3));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        // 쿠폰 발급 요청 10건 (레디스에 저장)
        for (int i=0; i<10; i++) {
            couponPublishRepository.savePublishRequest(new CouponPublishDTO(coupon.getId(), Long.valueOf(i), startDate, endDate));
        }

        // when

        // then
        // 레디스에서 5건 가져와 발급 요청
        assertThatThrownBy(() -> couponFacade.publishPendingRequests(coupon.getId(), 5))
                .isInstanceOf(CommerceConflictException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getMessage());
        // 3건은 발급 성공
        List<CouponPublish> publishedCoupons = couponPublishRepository.findAllByRefCouponId(coupon.getId());
        assertThat(publishedCoupons).hasSize(3) // 3건의 요청 건이 발급됨
                .extracting("refCouponId")
                .containsExactly(coupon.getId(), coupon.getId(), coupon.getId());
    }
}