package kr.hhplus.be.application.coupon;

import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponFacade {

    private final CouponService couponService;

    /**
     * 대기열에서 가져온 발급 요청 목록 -> 쿠폰 발급 요청
     */
    public void publishPendingRequests(long couponId, int requestCount) {
        List<CouponPublishDTO> couponPublishRequests = couponService.getCouponPublishRequests(couponId, requestCount);

        for (CouponPublishDTO publishDTO : couponPublishRequests) {
            couponService.publishCoupon(publishDTO);
            log.info("publishPendingRequests publishDTO:{}", publishDTO);
        }
    }
}
