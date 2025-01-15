package kr.hhplus.be.domain.exception;

import org.springframework.core.NestedRuntimeException;

public class CommerceNestedRuntimeException extends NestedRuntimeException {

    private final ErrorCode errorCode;

    public CommerceNestedRuntimeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CommerceNestedRuntimeException(String msg, ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

    public CommerceNestedRuntimeException(String msg, Throwable cause, ErrorCode errorCode) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
