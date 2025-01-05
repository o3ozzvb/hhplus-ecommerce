package kr.hhplus.be.support.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "잔액이 부족합니다."),
    CHARGE_AMOUNT_NOT_VALID("CHARGE_AMOUNT_NOT_VALID", "충전금액이 유효하지 않습니다."),
    INSUFFICIENT_INVENTORY("INSUFFICIENT_INVENTORY", "재고가 부족합니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 에러 입니다."),
    VALIDATION_ERROR("VALIDATION_ERROR", "필수값이 누락되었습니다.");

    private final String code;
    private final String message;

}
