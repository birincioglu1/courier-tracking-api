package com.birincioglu.couriertrackingapi.domain.exception;

import com.birincioglu.couriertrackingapi.domain.constant.ErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Getter
public class NotFoundException extends BaseBusinessException {
    private final ErrorCodes errorCode;

    public NotFoundException(String message) {
        super(ErrorCodes.BUSINESS, HttpStatus.BAD_REQUEST, message);
        this.errorCode = ErrorCodes.BUSINESS;
    }

    public NotFoundException(ErrorCodes errorCode, String message) {
        super(errorCode, HttpStatus.BAD_REQUEST, message);
        this.errorCode = errorCode;
    }
}
