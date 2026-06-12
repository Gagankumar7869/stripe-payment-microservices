package com.hulkhiretech.payments.constants;

public enum ErrorCodeEnum {

    GENERIC_ERROR("10000", "Validation failed"),

    // ==========================================
    // Payment URLs
    // ==========================================
    SUCCESS_URL_BLANK("10001", "successUrl must not be blank"),
    SUCCESS_URL_INVALID("10002", "successUrl must be a valid HTTP/HTTPS URL"),

    CANCEL_URL_BLANK("10003", "cancelUrl must not be blank"),
    CANCEL_URL_INVALID("10004", "cancelUrl must be a valid HTTP/HTTPS URL"),

    // ==========================================
    // Line Items
    // ==========================================
    LINE_ITEMS_EMPTY("10005", "lineItems must not be empty"),

    CURRENCY_BLANK("10006", "currency must not be blank"),
    CURRENCY_INVALID("10007", "currency must be a valid 3-letter ISO code"),

    PRODUCT_NAME_BLANK("10008", "productName must not be blank"),
    PRODUCT_NAME_TOO_LONG("10009", "productName must not exceed 100 characters"),

    UNIT_AMOUNT_NULL("10010", "unitAmount must not be null"),
    UNIT_AMOUNT_INVALID("10011", "unitAmount must be greater than 0"),

    QUANTITY_NULL("10012", "quantity must not be null"),
    QUANTITY_INVALID("10013", "quantity must be greater than 0"),

    // ==========================================
    // User
    // ==========================================
    USER_NULL("10014", "user must not be null"),

    END_USER_ID_BLANK("10015", "endUserID must not be blank"),
    END_USER_ID_INVALID("10016", "endUserID exceeds maximum allowed length"),

    FIRST_NAME_BLANK("10017", "firstname must not be blank"),
    FIRST_NAME_INVALID("10018", "firstname exceeds maximum allowed length"),

    LAST_NAME_BLANK("10019", "lastname must not be blank"),
    LAST_NAME_INVALID("10020", "lastname exceeds maximum allowed length"),

    EMAIL_BLANK("10021", "email must not be blank"),
    EMAIL_INVALID("10022", "email format is invalid"),

    MOBILE_PHONE_BLANK("10023", "mobilePhone must not be blank"),
    MOBILE_PHONE_INVALID("10024", "mobilePhone format is invalid"),

    // ==========================================
    // Payment Object
    // ==========================================
    PAYMENT_NULL("10025", "payment must not be null"),

    AMOUNT_NULL("10026", "amount must not be null"),
    AMOUNT_INVALID("10027", "amount must be greater than 0"),

    BRAND_NAME_BLANK("10028", "brandName must not be blank"),

    LOCALE_BLANK("10029", "locale must not be blank"),

    COUNTRY_BLANK("10030", "country must not be blank"),
    COUNTRY_INVALID("10031", "country must be a valid 2-letter ISO country code"),

    MERCHANT_TXN_REF_BLANK("10032", "merchantTxnRef must not be blank"),

    PAYMENT_METHOD_BLANK("10033", "paymentMethod must not be blank"),

    PROVIDER_BLANK("10034", "provider must not be blank"),

    PAYMENT_TYPE_BLANK("10035", "paymentType must not be blank");

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