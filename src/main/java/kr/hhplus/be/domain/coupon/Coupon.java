package kr.hhplus.be.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponName;

    private DiscountType discountType;

    private int discountValue;

    private int maxQuantity;

    private int remainQunatity;

    private CouponStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
