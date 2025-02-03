package kr.hhplus.be.domain.coupon.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponPublishDTO {
    private Long couponId;
    private Long userId;
    LocalDate validStartDate;
    LocalDate validEndDate;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;  // 같은 객체면 true
        if (obj == null || getClass() != obj.getClass()) return false; // 타입이 다르면 false

        CouponPublishDTO other = (CouponPublishDTO) obj;
        return Objects.equals(couponId, other.couponId) &&
                Objects.equals(userId, other.userId) &&
                Objects.equals(validStartDate, other.validStartDate) &&
                Objects.equals(validEndDate, other.validEndDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(couponId, userId, validStartDate, validEndDate);
    }
}
