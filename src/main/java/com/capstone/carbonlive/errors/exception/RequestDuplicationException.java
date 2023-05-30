package com.capstone.carbonlive.errors.exception;

import com.capstone.carbonlive.errors.ErrorCode;

public class RequestDuplicationException extends CommonException {
    public RequestDuplicationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
