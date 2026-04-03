package com.hulkhiretech.payments.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.ToString;

/**
 * Custom runtime exception for Stripe provider errors.
 * Contains an error code, an error message and an associated HTTP status.
 */
@Getter
@ToString(callSuper = true)
public class StripeProviderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private final String errorMessage; // kept the requested spelling
	private final HttpStatus httpStatus;

	public StripeProviderException(String errorCode, String errorMessage, HttpStatus httpStatus) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.httpStatus = httpStatus == null ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus;
	}



}
