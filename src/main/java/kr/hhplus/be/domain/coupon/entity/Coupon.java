package kr.hhplus.be.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.domain.coupon.enumtype.CouponStatus;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.exception.CommerceConflictException;
import kr.hhplus.be.domain.exception.ErrorCode;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponName;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private int discountValue;

    private int maxQuantity;

    private int remainQuantity;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static Coupon of(String couponName, DiscountType discountType, int discountValue, int initQuantity) {
        Coupon coupon = new Coupon();

        coupon.couponName = couponName;
        coupon.discountType = discountType;
        coupon.discountValue = discountValue;
        coupon.maxQuantity = initQuantity;
        coupon.remainQuantity = initQuantity;
        coupon.status = CouponStatus.ACTIVE;
        coupon.createdAt = LocalDateTime.now();
        coupon.updatedAt = LocalDateTime.now();

        return coupon;
    }

    public void publish() {
        if (this.remainQuantity <= 0) { // 잔여수량이 없으면
            throw new CommerceConflictException(ErrorCode.INSUFFICIENT_COUPON_QUANTITY);
        }
        this.updatedAt = LocalDateTime.now();
    }
}
