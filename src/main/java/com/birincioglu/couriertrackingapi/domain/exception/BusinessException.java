package com.birincioglu.couriertrackingapi.domain.exception;


import com.birincioglu.couriertrackingapi.domain.constant.ErrorCodes;
import org.springframework.http.HttpStatus;

public class BusinessException extends BaseBusinessException {
    public BusinessException(String message) {
        super(ErrorCodes.BUSINESS, HttpStatus.BAD_REQUEST, message);
    }

    public BusinessException(ErrorCodes errorCodes, String message) {
        super(errorCodes, HttpStatus.BAD_REQUEST, message);
    }
}
