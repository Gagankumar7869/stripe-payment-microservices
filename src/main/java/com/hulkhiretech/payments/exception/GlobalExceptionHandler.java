package com.hulkhiretech.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hulkhiretech.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler that converts application exceptions into consistent
 * HTTP responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(StripeProviderException.class)
    public ResponseEntity<ErrorResponse> handleStripeProviderException(StripeProviderException ex) {
        // Log the exception for diagnostics
        log.error("StripeProviderException caught: {} ", ex.toString());


        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        
        ErrorResponse resp = new ErrorResponse();
        resp.setErrorCode(ex.getErrorCode());
        resp.setErrorMessage(ex.getErrorMessage());
        
        log.error("Returning error response: {} with status {}", resp, status);
        return new ResponseEntity<>(resp, status);
    }

   
}
