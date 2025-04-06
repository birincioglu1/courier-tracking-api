package com.birincioglu.couriertrackingapi.presentation;

import com.birincioglu.couriertrackingapi.domain.constant.ErrorCodes;
import com.birincioglu.couriertrackingapi.domain.exception.BaseBusinessException;
import com.birincioglu.couriertrackingapi.presentation.model.error.ErrorDetail;
import com.birincioglu.couriertrackingapi.presentation.model.error.ErrorResponse;
import com.birincioglu.couriertrackingapi.presentation.model.response.Response;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({BaseBusinessException.class})
    public ResponseEntity<Response<Object>> handleBusinessException(BaseBusinessException ex) {
        return getResponse(ex.getClass().getSimpleName(), ex.getErrorCode(), ex.getHttpStatus(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Response<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return handleValidationException(ex.getBindingResult(), ex.getClass().getSimpleName());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Response<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        return getResponse(ex.getClass().getSimpleName(), ErrorCodes.REQUEST_VALIDATION, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({DataRetrievalFailureException.class})
    public ResponseEntity<Response<Object>> handleConstraintViolationException(DataRetrievalFailureException ex) {
        return getResponse(ex.getClass().getSimpleName(), ErrorCodes.NOT_FOUND, HttpStatus.NOT_FOUND, "{0}: {1}", ex.getClass().getSimpleName(), ex.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Response<Object>> handleException(Exception ex) {
        return getResponse(ex.getClass().getSimpleName(), ErrorCodes.UNDEFINED, HttpStatus.INTERNAL_SERVER_ERROR, "{0}: {1}", ex.getClass().getSimpleName(), ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return getResponse(ex.getClass().getSimpleName(), ErrorCodes.USERNAME_ALREADY_TAKEN, HttpStatus.CONFLICT, ErrorCodes.USERNAME_ALREADY_TAKEN.name());
    }

    private ResponseEntity<Response<Object>> handleValidationException(BindingResult bindingResult, String simpleName) {
        List<ErrorDetail> errorDetails = bindingResult.getAllErrors().stream().map(error -> {
            String fieldName = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
            return ErrorDetail.builder()
                    .code(ErrorCodes.REQUEST_VALIDATION.getCode())
                    .title(fieldName + ": " + error.getDefaultMessage())
                    .type(simpleName)
                    .build();
        }).toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(errorDetails).build();
        return getResponse(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Response<Object>> getResponse(String type, ErrorCodes errorCodes, HttpStatus statusCode, String message, Object... parameters) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(errorCodes.getCode())
                .errors(Collections.singletonList(ErrorDetail.builder()
                        .code(errorCodes.getCode())
                        .title(MessageFormat.format(message, parameters))
                        .type(type)
                        .build()))
                .build();
        return getResponse(errorResponse, statusCode);
    }

    private ResponseEntity<Response<Object>> getResponse(ErrorResponse errorResponse, HttpStatus statusCode) {
        Response<Object> response = new Response<>();
        response.setError(errorResponse);
        response.getError().setStatus(statusCode.value());
        log.error("GlobalExceptionHandler errorResponse: {}, statusCode: {}", errorResponse, statusCode);
        return new ResponseEntity<>(response, statusCode);
    }
}
