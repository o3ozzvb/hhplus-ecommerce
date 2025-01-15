package kr.hhplus.be.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CommerceConflictException extends CommerceNestedRuntimeException {
    public CommerceConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommerceConflictException(String msg, ErrorCode errorCode) {
        super(msg, errorCode);
    }

    public CommerceConflictException(String msg, Throwable cause, ErrorCode errorCode) {
        super(msg, cause, errorCode);
    }

    @Override
    public ErrorCode getErrorCode() {
        return super.getErrorCode();
    }
}
