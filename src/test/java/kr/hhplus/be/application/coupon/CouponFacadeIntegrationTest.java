package kr.hhplus.be.application.coupon;

import kr.hhplus.be.support.DatabaseCleanup;
import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.coupon.service.CouponService;
import kr.hhplus.be.domain.exception.CommerceConflictException;
import kr.hhplus.be.domain.exception.ErrorCode;
import kr.hhplus.be.support.RedisCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
class CouponFacadeIntegrationTest {

    @Autowired
    private CouponFacade couponFacade;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponPublishRepository couponPublishRepository;
    @Autowired
    private DatabaseCleanup databaseCleanup;
    @Autowired
    private RedisCleanup redisCleanup;

    @BeforeEach
    void cleanUpDatabase() {
        databaseCleanup.execute();
        redisCleanup.execute();
    }

    @Test
    @DisplayName("레디스에서 쿠폰발급 요청 N건을 가져와 발급요청하면 N개의 쿠폰이 발급된다.")
    void publishPendingRequestsTest() {
        // given
        Coupon coupon = couponService.saveCoupon(Coupon.of("선착순쿠폰", DiscountType.FIXED_AMOUNT, 10000, 30));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        // 쿠폰 발급 요청 3건 (레디스에 저장)
        for (int i=1; i<=10; i++) {
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
    @DisplayName("잔여수량 10개인 쿠폰에 대해 3명의 사용자가 발급을 요청하면 레디스 대기열에 요청이 저장되며, 레디스에 적재된 쿠폰의 잔여수량이 차감되어 7이 된다.")
    void addPendingRequestsTest() {
        // given
        Coupon coupon = couponService.saveCoupon(Coupon.of("선착순쿠폰", DiscountType.FIXED_AMOUNT, 10000, 10));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        // 쿠폰 발급 요청 3건
        for (int i=1; i<=3; i++) {
            couponService.addCouponPublishRequest(new CouponPublishDTO(coupon.getId(), Long.valueOf(i), startDate, endDate));
        }

        // when

        // then
        // 3건은 대기열 저장 성공 (발급 요청 유저 SET의 원소 개수 조회)
        Integer couponPublishRequestCount = couponPublishRepository.getCouponPublishCount(coupon.getId());
        assertThat(couponPublishRequestCount).isEqualTo(3);

        // 캐시에 적재된 캐시의 잔여수량은 0
        Integer cacheRemainQuantity = couponRepository.getCacheRemainQuantity(coupon.getId());
        assertThat(cacheRemainQuantity).isEqualTo(7);
    }

    @Test
    @DisplayName("잔여수량 0건인 쿠폰에 대해 발급을 요청하면 CommerceConflictException이 발생한다.")
    void publishPendingRequestsExceptionTest() {
        // given
        Coupon coupon = couponService.saveCoupon(Coupon.of("선착순쿠폰", DiscountType.FIXED_AMOUNT, 10000, 0));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        // when

        // then
        assertThatThrownBy(() -> couponService.addCouponPublishRequest(new CouponPublishDTO(coupon.getId(), 1L, startDate, endDate)))
                .isInstanceOf(CommerceConflictException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getMessage());
    }
}