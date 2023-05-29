package com.capstone.carbonlive.security.jwt;

import com.capstone.carbonlive.errors.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JwtException extends RuntimeException {
    private ErrorCode errorCode;
    private HttpStatus httpStatus;
    private String message;

    public JwtException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}
