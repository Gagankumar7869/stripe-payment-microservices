package com.hulkhiretech.payments.constant;

/**
 * Centralized error codes and one-line messages for validation errors.
 */
public enum ErrorCodeEnum {
    MISSING_REQUEST_BODY("30001", "Missing request body: CreatePaymentReq"),
    MISSING_SUCCESS_URL("30002", "Missing required field: successUrl"),
    MISSING_CANCEL_URL("30003", "Missing required field: cancelUrl"),
    MISSING_LINE_ITEMS("30004", "Missing required field: lineItems"),
    INVALID_SUCCESS_URL_FORMAT("30005", "Invalid URL format: successUrl"),
    INVALID_CANCEL_URL_FORMAT("30006", "Invalid URL format: cancelUrl"),
    MISSING_PRODUCT_NAME("30007", "Missing required field: productName in line item"),
    MISSING_CURRENCY("30008", "Missing required field: currency in line item"),
    INVALID_CURRENCY_CODE("30009", "Invalid currency code (expected 3 letters) in line item"),
    INVALID_UNIT_AMOUNT("30010", "Invalid unitAmount (must be > 0) in line item"),
    INVALID_QUANTITY("30011", "Invalid quantity (must be > 0) in line item");

    private final String errorCode;
    private final String errorMessage;

    ErrorCodeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {  
        return errorMessage;
    }
}
