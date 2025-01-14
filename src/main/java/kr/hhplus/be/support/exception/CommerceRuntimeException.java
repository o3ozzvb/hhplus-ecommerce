package kr.hhplus.be.support.exception;

public class CommerceRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;

    public CommerceRuntimeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorCodeString() {
        return errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
