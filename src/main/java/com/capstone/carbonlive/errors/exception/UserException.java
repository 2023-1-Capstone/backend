package com.capstone.carbonlive.errors.exception;

import com.capstone.carbonlive.errors.ErrorCode;

public class UserException extends CommonException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
