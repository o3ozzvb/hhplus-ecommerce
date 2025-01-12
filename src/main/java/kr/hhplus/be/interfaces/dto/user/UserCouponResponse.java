package kr.hhplus.be.interfaces.dto.user;

import kr.hhplus.be.domain.coupon.enumtype.CouponPublishStatus;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserCouponResponse {
    private long couponPublishId;

    private long couponId;

    private String couponName;

    private DiscountType discountType;

    private int discountValue;

    private LocalDate publishDate;

    private LocalDate validStartDate;

    private LocalDate validEndDate;

    private CouponPublishStatus status;

    public static UserCouponResponse from(UserCouponDTO userCouponDTO) {
        return UserCouponResponse.builder()
                .couponPublishId(userCouponDTO.getCouponPublishId())
                .couponId(userCouponDTO.getCouponId())
                .couponName(userCouponDTO.getCouponName())
                .discountType(userCouponDTO.getDiscountType())
                .discountValue(userCouponDTO.getDiscountValue())
                .publishDate(userCouponDTO.getPublishDate())
                .validStartDate(userCouponDTO.getValidStartDate())
                .validEndDate(userCouponDTO.getValidEndDate())
                .status(userCouponDTO.getStatus()).build();
    }
}
