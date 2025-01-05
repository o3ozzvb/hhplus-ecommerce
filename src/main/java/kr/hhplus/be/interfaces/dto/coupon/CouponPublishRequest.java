package kr.hhplus.be.interfaces.dto.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponPublishRequest {
    @NotNull(message = "쿠폰 ID는 필수값 입니다.")
    private Long couponId;

    @NotNull(message = "사용자 ID는 필수값 입니다.")
    private Long userId;

    @NotNull(message = "유효시작일자는 필수값 입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate validStartDate;

    @NotNull(message = "유효종료일자는 필수값 입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate validEndDate;
}
