package com.capstone.carbonlive.errors.exception;

import com.capstone.carbonlive.errors.ErrorCode;

public class TokenException extends CommonException {
    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
