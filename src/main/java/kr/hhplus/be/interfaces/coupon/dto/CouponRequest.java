package kr.hhplus.be.interfaces.coupon.dto;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.domain.coupon.dto.CouponSearchDTO;
import kr.hhplus.be.domain.coupon.enumtype.CouponStatus;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponRequest {
    @NotNull(message = "사용자 ID는 필수값 입니다.")
    private Long userId;

    private String couponName;
    private DiscountType discountType;
    private LocalDate startDate;
    private LocalDate endDate;
    private CouponStatus status;

    // paging info
    @NotNull
    private Integer page;
    @NotNull
    private Integer size;

    public CouponSearchDTO toDTO() {
        return CouponSearchDTO.builder()
                .userId(this.userId)
                .build();
    }
}
