package kr.hhplus.be.interfaces.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.hhplus.be.domain.user.dto.BalanceDTO;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import kr.hhplus.be.domain.user.service.UserService;
import kr.hhplus.be.interfaces.common.ApiResponse;
import kr.hhplus.be.interfaces.coupon.dto.CouponRequest;
import kr.hhplus.be.interfaces.user.dto.BalanceResponse;
import kr.hhplus.be.interfaces.user.dto.ChargeRequest;
import kr.hhplus.be.interfaces.user.dto.UserCouponResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    /**
     * 잔액 충전
     */
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    @PostMapping("/users/{userId}/charge")
    public ApiResponse<BalanceResponse> charge(@PathVariable("userId") long userId,
                                               @RequestBody @Valid ChargeRequest request) {
        log.debug("request ; {}", request);

        BalanceDTO balanceDTO = userService.charge(userId, request.getChargeAmount());

        return ApiResponse.success(BalanceResponse.from(balanceDTO));
    }

    /**
     * 잔액 조회
     */
    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    @GetMapping("/users/{userId}/balance")
    public ApiResponse<BalanceResponse> getUserBalance(@PathVariable("userId") long userId) {

        BalanceDTO balanceDTO = userService.getBalance(userId);

        return ApiResponse.success(BalanceResponse.from(balanceDTO));
    }

    /**
     * 보유 쿠폰 목록 조회
     */
    @Operation(summary = "보유 쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
    @GetMapping("/users/{userId}/coupons")
    public ApiResponse<Page<UserCouponResponse>> getUserCoupons(@PathVariable("userId") long userId
            , @Valid CouponRequest request) {
        request.setUserId(userId);
        Page<UserCouponDTO> userCoupons = userService.getUserCoupons(request.toDTO(), PageRequest.of(request.getPage(), request.getSize()));

        List<UserCouponDTO> userCouponList = userCoupons.getContent();
        List<UserCouponResponse> response = userCouponList.stream()
                .map(userCoupon -> UserCouponResponse.from(userCoupon))
                .collect(Collectors.toList());

        return ApiResponse.success(new PageImpl<>(response, userCoupons.getPageable(), userCoupons.getTotalElements()));
    }
}
