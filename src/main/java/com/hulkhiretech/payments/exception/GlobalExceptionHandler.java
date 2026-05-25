package com.hulkhiretech.payments.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hulkhiretech.payments.constants.ErrorCodeEnum;
import com.hulkhiretech.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(
			MethodArgumentNotValidException ex) {


		log.error("Validation error: ", ex);

		FieldError fieldError = ex.getBindingResult()
				.getFieldErrors()
				.get(0); 

		String enumKey = fieldError.getDefaultMessage();

		ErrorCodeEnum errorCodeEnum;
		try {
			errorCodeEnum = ErrorCodeEnum.valueOf(enumKey);
		} catch (IllegalArgumentException | NullPointerException e) {
			errorCodeEnum = ErrorCodeEnum.GENERIC_ERROR;
		}

		ErrorResponse response = new ErrorResponse(
				errorCodeEnum.getErrorCode(),
				errorCodeEnum.getErrorMessage()
				);
		log.error("Returning validation error response: {} with status {}", response, 400);

		return ResponseEntity.badRequest().body(response);
	}
}