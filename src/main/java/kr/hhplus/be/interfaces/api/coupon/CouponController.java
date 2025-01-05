package kr.hhplus.be.interfaces.api.coupon;

import kr.hhplus.be.interfaces.dto.common.ApiResponse;
import kr.hhplus.be.interfaces.dto.coupon.CouponPublishRequest;
import kr.hhplus.be.interfaces.dto.coupon.CouponPublishResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class CouponController {

    /**
     * 쿠폰 발급 요청
     */
    @PostMapping("/coupons/publish")
    public ApiResponse<CouponPublishResponse> publishCoupon(@RequestBody CouponPublishRequest request) {
        log.debug("CouponController publishCoupon - request: {}", request);

        CouponPublishResponse response = CouponPublishResponse.builder()
                .couponPublishId(1L).build();

        return ApiResponse.success(response);
    }
}
