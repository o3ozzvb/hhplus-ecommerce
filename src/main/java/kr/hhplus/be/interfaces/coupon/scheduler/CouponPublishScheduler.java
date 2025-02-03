package kr.hhplus.be.interfaces.coupon.scheduler;

import kr.hhplus.be.application.coupon.CouponFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponPublishScheduler {

    private final CouponFacade couponFacade;

    @Scheduled(fixedRate = 1000) // 1초마다 요청 N개 처리
    public void processCouponPublish() {
        couponFacade.publishPendingRequests(1L, 10);
    }
}

