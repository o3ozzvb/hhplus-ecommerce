package kr.hhplus.be.domain.coupon.service;

import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("쿠폰 발급 요청 시 Redis 대기열에 요청이 추가된다")
    public void CouponPublishRedisTest() {
        //given
        long userId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        Coupon coupon = couponRepository.save(Coupon.of("쿠폰", DiscountType.FIXED_RATE, 10, 10));

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
        assertThat(publishRequest.get(0)).isEqualTo(new CouponPublishDTO(coupon.getId(), userId, startDate, endDate));
    }
}
