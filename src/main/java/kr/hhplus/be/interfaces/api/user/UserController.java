package kr.hhplus.be.interfaces.api.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.hhplus.be.domain.coupon.CouponStatus;
import kr.hhplus.be.domain.coupon.DiscountType;
import kr.hhplus.be.interfaces.dto.common.ApiResponse;
import kr.hhplus.be.interfaces.dto.coupon.CouponInfo;
import kr.hhplus.be.interfaces.dto.coupon.CouponRequest;
import kr.hhplus.be.interfaces.dto.coupon.CouponResponse;
import kr.hhplus.be.interfaces.dto.user.BalanceResponse;
import kr.hhplus.be.interfaces.dto.user.ChargeRequest;
import kr.hhplus.be.interfaces.dto.user.ChargeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class UserController {

    /**
     * 잔액 충전
     */
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    @PostMapping("/users/{userId}/charge")
    public ApiResponse<ChargeResponse> charge(@RequestBody @Valid ChargeRequest request) {
        log.debug("request ; {}" , request);
        ChargeResponse response = ChargeResponse.builder()
                        .balance(request.getChargeAmount()).build();

        return ApiResponse.success(response);
    }

    /**
     * 잔액 조회
     */
    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    @GetMapping("/users/{userId}/balance")
    public ApiResponse<BalanceResponse> getUserBalance(@PathVariable("userId") long userId) {
        BalanceResponse response = BalanceResponse.builder()
                .userId(userId)
                .balance(10000).build();

        return ApiResponse.success(response);
    }

    /**
     * 보유 쿠폰 목록 조회
     */
    @Operation(summary = "보유 쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
    @GetMapping("/users/{userId}/coupons")
    public ApiResponse<CouponResponse> getUserCoupons(CouponRequest request) {
        List<CouponInfo> couponList = new ArrayList<>();

        couponList.add(new CouponInfo(1L, "쿠폰1", LocalDate.of(2024,12,31), DiscountType.FIXED_AMOUNT, 20000, LocalDate.of(2025,1,1), LocalDate.of(2025,1,31), CouponStatus.AVAILABLE));
        couponList.add(new CouponInfo(2L, "쿠폰2", LocalDate.of(2024,12,31), DiscountType.FIXED_AMOUNT, 5000, LocalDate.of(2025,1,1), LocalDate.of(2025,1,31), CouponStatus.EXPIRED));
        couponList.add(new CouponInfo(3L, "쿠폰3", LocalDate.of(2024,12,25), DiscountType.FIXED_AMOUNT, 10000, LocalDate.of(2025,1,1), LocalDate.of(2025,1,31), CouponStatus.REDEEMED));
        couponList.add(new CouponInfo(4L, "쿠폰4", LocalDate.of(2024,12,30), DiscountType.FIXED_RATE, 15, LocalDate.of(2025,1,1), LocalDate.of(2025,1,31), CouponStatus.AVAILABLE));
        couponList.add(new CouponInfo(5L, "쿠폰5", LocalDate.of(2025,1,1), DiscountType.FIXED_RATE, 10, LocalDate.of(2025,1,1), LocalDate.of(2025,1,31), CouponStatus.AVAILABLE));

        return ApiResponse.success(new CouponResponse(couponList));
    }
}
