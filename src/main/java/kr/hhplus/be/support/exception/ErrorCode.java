package kr.hhplus.be.support.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 에러 입니다."),
    VALIDATION_ERROR("VALIDATION_ERROR", "필수값이 누락되었습니다.");

    private final String code;
    private final String message;

}
