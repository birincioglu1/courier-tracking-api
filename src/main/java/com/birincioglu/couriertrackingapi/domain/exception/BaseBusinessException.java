package com.birincioglu.couriertrackingapi.domain.exception;

import com.birincioglu.couriertrackingapi.domain.constant.ErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseBusinessException extends RuntimeException {
    private final ErrorCodes errorCode;
    private final HttpStatus httpStatus;

    public BaseBusinessException(ErrorCodes errorCode, HttpStatus httpStatus, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
