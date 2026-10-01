package com.gagan.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gagan.payments.constant.ErrorCodeEnum;
import com.gagan.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


	@ExceptionHandler(StripeProviderException.class)
	public ResponseEntity<ErrorResponse> handleStripeProviderException(StripeProviderException ex) {
		log.error("StripeProviderException caught: {} ", ex.toString());


		HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;

		ErrorResponse resp = new ErrorResponse();
		resp.setErrorCode(ex.getErrorCode());
		resp.setErrorMessage(ex.getErrorMessage());

		log.error("Returning error response: {} with status {}", resp, status);
		return new ResponseEntity<>(resp, status);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		log.error("Generic exception caught: ", ex);

		ErrorResponse resp = new ErrorResponse();
		resp.setErrorCode(ErrorCodeEnum.GENERIC_ERROR.getErrorCode()); 
		resp.setErrorMessage(ErrorCodeEnum.GENERIC_ERROR.getErrorMessage());

		log.error("Returning generic error response: {} with status {}", resp, HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
