package com.threefour.common;

public class ExpectedException extends RuntimeException {

    private final ErrorCode errorCode;

    public ExpectedException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}