package com.capstone.carbonlive.errors.exception;

import com.capstone.carbonlive.errors.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CommonException extends RuntimeException {
    private ErrorCode errorCode;
    private HttpStatus httpStatus;
    private String message;

    public CommonException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}
