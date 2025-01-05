package kr.hhplus.be.interfaces.dto.coupon;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.domain.coupon.CouponStatus;
import kr.hhplus.be.domain.coupon.DiscountType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponRequest {
    @NotNull(message = "사용자 ID는 필수값 입니다.")
    private Long userId;

    private String couponName;
    private DiscountType discountType;
    private LocalDate startDate;
    private LocalDate endDate;
    private CouponStatus status;
}
