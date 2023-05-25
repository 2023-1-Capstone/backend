package com.capstone.carbonlive.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private ErrorCode errorCode;

    public UserException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
