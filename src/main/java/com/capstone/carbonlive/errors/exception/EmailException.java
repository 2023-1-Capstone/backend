package com.capstone.carbonlive.errors.exception;

import com.capstone.carbonlive.errors.ErrorCode;

public class EmailException extends CommonException {
    public EmailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
