package kr.hhplus.be.interfaces.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.service.CouponService;
import kr.hhplus.be.interfaces.common.ApiResponse;
import kr.hhplus.be.interfaces.coupon.dto.CouponPublishRequest;
import kr.hhplus.be.interfaces.coupon.dto.CouponPublishResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CouponController {

    private final CouponService couponService;

    /**
     * 쿠폰 발급 요청
     */
    @Operation(summary = "쿠폰 발급 요청", description = "쿠폰 발급을 요청합니다.")
    @PostMapping("/coupons/publish")
    public ApiResponse<CouponPublishResponse> publishCoupon(@RequestBody @Valid CouponPublishRequest request) {
        log.debug("CouponController publishCoupon - request: {}", request);

        CouponPublish couponPublish = couponService.publishCoupon(request.toCommand());

        return ApiResponse.success(CouponPublishResponse.from(couponPublish));
    }

}
