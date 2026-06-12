package com.hulkhiretech.payments.pojo;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Payment {

    @NotBlank(message = "CURRENCY_BLANK")
    @Pattern(regexp = "^[A-Z]{3}$", message = "CURRENCY_INVALID")
    private String currency;

    @NotNull(message = "AMOUNT_NULL")
    @Positive(message = "AMOUNT_INVALID")
    private Long amount;

    @NotBlank(message = "BRAND_NAME_BLANK")
    private String brandName;

    @NotBlank(message = "LOCALE_BLANK")
    private String locale;

    @NotBlank(message = "COUNTRY_BLANK")
    @Pattern(regexp = "^[A-Z]{2}$", message = "COUNTRY_INVALID")
    private String country;

    @NotBlank(message = "MERCHANT_TXN_REF_BLANK")
    private String merchantTxnRef;

    @NotBlank(message = "PAYMENT_METHOD_BLANK")
    private String paymentMethod;

    @NotBlank(message = "PROVIDER_BLANK")
    private String provider;

    @NotBlank(message = "PAYMENT_TYPE_BLANK")
    private String paymentType;

    @NotBlank(message = "SUCCESS_URL_BLANK")
    @Pattern(
        regexp = "^(http|https)://.*$",
        message = "SUCCESS_URL_INVALID"
    )
    private String successUrl;

    @NotBlank(message = "CANCEL_URL_BLANK")
    @Pattern(
        regexp = "^(http|https)://.*$",
        message = "CANCEL_URL_INVALID"
    )
    private String cancelUrl;

    @NotEmpty(message = "LINE_ITEMS_EMPTY")
    @Valid
    private List<LineItem> lineItems;
}