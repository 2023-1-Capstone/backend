package com.capstone.carbonlive.exception;

public class EmailException extends RuntimeException {

    private ErrorCode errorCode;

    public EmailException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
