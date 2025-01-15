package kr.hhplus.be.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CommerceNotFoundException extends CommerceNestedRuntimeException {
    public CommerceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommerceNotFoundException(String msg, ErrorCode errorCode) {
        super(msg, errorCode);
    }

    public CommerceNotFoundException(String msg, Throwable cause, ErrorCode errorCode) {
        super(msg, cause, errorCode);
    }

    @Override
    public ErrorCode getErrorCode() {
        return super.getErrorCode();
    }
}
