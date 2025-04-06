package com.birincioglu.couriertrackingapi.domain.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCodes {
    UNDEFINED(0),
    BUSINESS(1000),
    NOT_FOUND(1001),
    REQUEST_VALIDATION(1003),
    USERNAME_ALREADY_TAKEN(1004);

    private final int code;

    ErrorCodes(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }
}