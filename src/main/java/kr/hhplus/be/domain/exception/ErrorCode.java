package kr.hhplus.be.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // USER
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "잔액이 부족합니다."),
    CHARGE_AMOUNT_NOT_VALID("CHARGE_AMOUNT_NOT_VALID", "충전금액이 유효하지 않습니다."),
    USER_NOT_EXIST("USER_NOT_EXIST", "존재하지 않는 사용자입니다."),
    // PRODUCT
    INSUFFICIENT_INVENTORY("INSUFFICIENT_INVENTORY", "재고가 부족합니다."),
    PRODUCT_INVENTORY_NOT_EXIST("PRODUCT_INVENTORY_NOT_EXIST", "상품 재고 정보가 존재하지 않습니다."),
    // COUPON
    INSUFFICIENT_COUPON_QUANTITY("INSUFFICIENT_COUPON_QUANTITY", "쿠폰을 더 이상 발행할 수 없습니다."),
    COUPON_NOT_EXIST("COUPON_NOT_EXIST", "존재하지 않는 쿠폰입니다."),
    COUPON_NOT_AVAILABLE("COUPON_NOT_AVAILABLE", "쿠폰을 사용할 수 없습니다."),
    COUPON_VALID_DATE_EXPIRED("COUPON_VALID_DATE_EXPIRED", "쿠폰 유효 기간이 만료되었습니다."),
    // ORDER
    ORDER_NOT_EXIST("ORDER_NOT_EXIST", "존재하지 않는 주문입니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 에러 입니다."),
    VALIDATION_ERROR("VALIDATION_ERROR", "필수값이 누락되었습니다."),
    // PAYMENT
    PAYMENT_NOT_EXIST("PAYMENT_NOT_EXIST", "존재하지 않는 결제입니다.");

    private final String code;
    private final String message;

}
