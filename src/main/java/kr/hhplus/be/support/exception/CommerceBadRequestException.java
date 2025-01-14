package kr.hhplus.be.support.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommerceBadRequestException extends CommerceNestedRuntimeException {
    public CommerceBadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommerceBadRequestException(String msg, ErrorCode errorCode) {
        super(msg, errorCode);
    }

    public CommerceBadRequestException(String msg, Throwable cause, ErrorCode errorCode) {
        super(msg, cause, errorCode);
    }

    @Override
    public ErrorCode getErrorCode() {
        return super.getErrorCode();
    }
}
