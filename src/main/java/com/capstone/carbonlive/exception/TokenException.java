package com.capstone.carbonlive.exception;

public class TokenException extends RuntimeException {

    private ErrorCode errorCode;

    public TokenException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
