package kr.hhplus.be.domain.coupon.service;

import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.entity.Coupon;
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
public class CouponIntegrationTest {

    @Autowired
    CouponService couponService;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    CouponPublishRepository couponPublishRepository;

    @Test
    @DisplayName("쿠폰 발급 요청 시 Redis sorted set에 요청이 추가되고 set에 유저가 추가되고 쿠폰의 잔여수량(캐시 & 디비)이 차감된다")
    public void CouponPublishRedisTest() {
        //given
        long userId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        Coupon coupon = couponService.saveCoupon(Coupon.of("쿠폰", DiscountType.FIXED_RATE, 10, 10));

        CouponPublishDTO publishDTO = CouponPublishDTO.builder()
                .couponId(coupon.getId())
                .userId(userId)
                .validStartDate(startDate)
                .validEndDate(endDate)
                .build();

        //when
        couponService.addCouponPublishRequest(publishDTO);

        //then
        List<CouponPublishDTO> publishRequest = couponPublishRepository.getPublishRequest(coupon.getId(), 1);
        assertThat(publishRequest).hasSize(1); // 대기열(sorted set)에 적재여부 검증

        Integer couponPublishCount = couponPublishRepository.getCouponPublishCount(coupon.getId());
        assertThat(couponPublishCount).isEqualTo(1); // 발급 요청 유저 (set) 수 검증

        Integer cacheRemainQuantity = couponRepository.getCacheRemainQuantity(coupon.getId());
        assertThat(cacheRemainQuantity).isEqualTo(coupon.getRemainQuantity() - 1); // 잔여수량 = 처음 잔여수량 - 발급요청수량(1)

        Coupon findCoupon = couponRepository.findById(coupon.getId());
        assertThat(findCoupon.getRemainQuantity()).isEqualTo(coupon.getRemainQuantity() - 1); // 잔여수량 = 처음 잔여수량 - 발급요청수량(1)
    }

    @Test
    @DisplayName("잔여수량이 0인 쿠폰 발급 요청 시 CommerceConflictException 이 발생한다.")
    public void NoRemainQuantityCouponPublishRequestFailTest() {
        //given
        long userId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        Coupon coupon = couponService.saveCoupon(Coupon.of("쿠폰", DiscountType.FIXED_RATE, 10, 0));

        CouponPublishDTO publishDTO = CouponPublishDTO.builder()
                .couponId(coupon.getId())
                .userId(userId)
                .validStartDate(startDate)
                .validEndDate(endDate)
                .build();

        //when

        //then
        assertThatThrownBy(() -> couponService.addCouponPublishRequest(publishDTO))
                .isInstanceOf(CommerceConflictException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getMessage());
    }

    @Test
    @DisplayName("쿠폰 발급 요청 시 이미 발급 요청한 user 이면 CommerceConflictException 이 발생한다.")
    public void DuplicateCouponPublishRequestFailTest() {
        //given
        long userId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        Coupon coupon = couponService.saveCoupon(Coupon.of("쿠폰", DiscountType.FIXED_RATE, 10, 10));

        CouponPublishDTO publishDTO = CouponPublishDTO.builder()
                .couponId(coupon.getId())
                .userId(userId)
                .validStartDate(startDate)
                .validEndDate(endDate)
                .build();
        couponService.addCouponPublishRequest(publishDTO); // 발급 요청

        //when

        //then
        assertThatThrownBy(() -> couponService.addCouponPublishRequest(publishDTO)) // 동일한 발급 요청
                .isInstanceOf(CommerceConflictException.class)
                .hasMessage(ErrorCode.ALREAY_REQUESTED_COUPON.getMessage());
    }
}
