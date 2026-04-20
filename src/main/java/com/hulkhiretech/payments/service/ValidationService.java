package com.hulkhiretech.payments.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;
import com.hulkhiretech.payments.pojo.CreatePaymentReq;
import com.hulkhiretech.payments.pojo.LineItem;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ValidationService {

    /**
     * Validate the CreatePaymentReq. Throws StripeProviderException with sequential
     * error codes starting at 30001 for each validation failure.
     */
    public void isValid(CreatePaymentReq req) {
        if(req == null) {
            log.error("CreatePaymentReq is null");
            throw new StripeProviderException(
                    ErrorCodeEnum.MISSING_REQUEST_BODY.getErrorCode(),
                    ErrorCodeEnum.MISSING_REQUEST_BODY.getErrorMessage(),
                    HttpStatus.BAD_REQUEST
                    );
        }

        // 30002
        if (req.getSuccessUrl() == null || req.getSuccessUrl().trim().isEmpty()) {
            log.error("Missing successUrl in CreatePaymentReq");
            throw new StripeProviderException(
                    ErrorCodeEnum.MISSING_SUCCESS_URL.getErrorCode(),
                    ErrorCodeEnum.MISSING_SUCCESS_URL.getErrorMessage(),
                    HttpStatus.BAD_REQUEST
                    );
        }

        // 30003
        if (req.getCancelUrl() == null || req.getCancelUrl().trim().isEmpty()) {
            log.error("Missing cancelUrl in CreatePaymentReq");
            throw new StripeProviderException(
                    ErrorCodeEnum.MISSING_CANCEL_URL.getErrorCode(),
                    ErrorCodeEnum.MISSING_CANCEL_URL.getErrorMessage(),
                    HttpStatus.BAD_REQUEST
                    );
        }

        // 30004
        if (req.getLineItems() == null || req.getLineItems().isEmpty()) {
            log.error("Missing or empty lineItems in CreatePaymentReq");
            throw new StripeProviderException(
                    ErrorCodeEnum.MISSING_LINE_ITEMS.getErrorCode(),
                    ErrorCodeEnum.MISSING_LINE_ITEMS.getErrorMessage(),
                    HttpStatus.BAD_REQUEST
                    );
        }

        // Validate URLs format
        try {
            new URL(req.getSuccessUrl());
        } catch (MalformedURLException e) {
            log.error("Invalid successUrl format: {}", req.getSuccessUrl());
            throw new StripeProviderException(
                    ErrorCodeEnum.INVALID_SUCCESS_URL_FORMAT.getErrorCode(),
                    ErrorCodeEnum.INVALID_SUCCESS_URL_FORMAT.getErrorMessage(),
                    HttpStatus.BAD_REQUEST
                    );
        }

        try {
            new URL(req.getCancelUrl());
        } catch (MalformedURLException e) {
            log.error("Invalid cancelUrl format: {}", req.getCancelUrl());
            throw new StripeProviderException(
                    ErrorCodeEnum.INVALID_CANCEL_URL_FORMAT.getErrorCode(),
                    ErrorCodeEnum.INVALID_CANCEL_URL_FORMAT.getErrorMessage(),
                    HttpStatus.BAD_REQUEST
                    );
        }

        // Validate each line item using for-each while keeping an index for messages
        List<LineItem> items = req.getLineItems();
        int i = 0;
        for (LineItem item : items) {
            String prefix = "lineItems[%d]".formatted(i);

            // 30007 - productName
            if (item.getProductName() == null || item.getProductName().trim().isEmpty()) {
                log.error("{} productName is missing", prefix);
                throw new StripeProviderException(
                        ErrorCodeEnum.MISSING_PRODUCT_NAME.getErrorCode(),
                        ErrorCodeEnum.MISSING_PRODUCT_NAME.getErrorMessage() + " at index " + i,
                        HttpStatus.BAD_REQUEST
                        );
            }

            // 30008 - currency
            if (item.getCurrency() == null || item.getCurrency().trim().isEmpty()) {
                log.error("{} currency is missing", prefix);
                throw new StripeProviderException(
                        ErrorCodeEnum.MISSING_CURRENCY.getErrorCode(),
                        ErrorCodeEnum.MISSING_CURRENCY.getErrorMessage() + " at index " + i,
                        HttpStatus.BAD_REQUEST
                        );
            }
            String currency = item.getCurrency().trim();
            if (currency.length() != 3 || !currency.chars().allMatch(Character::isLetter)) {
                log.error("{} currency invalid: {}", prefix, currency);
                throw new StripeProviderException(
                        ErrorCodeEnum.INVALID_CURRENCY_CODE.getErrorCode(),
                        ErrorCodeEnum.INVALID_CURRENCY_CODE.getErrorMessage() + " at index " + i,
                        HttpStatus.BAD_REQUEST
                        );
            }

            // 30010 - unitAmount
            if (item.getUnitAmount() <= 0) {
                log.error("{} unitAmount invalid: {}", prefix, item.getUnitAmount());
                throw new StripeProviderException(
                        ErrorCodeEnum.INVALID_UNIT_AMOUNT.getErrorCode(),
                        ErrorCodeEnum.INVALID_UNIT_AMOUNT.getErrorMessage() + " at index " + i,
                        HttpStatus.BAD_REQUEST
                        );
            }

            // 30011 - quantity
            if (item.getQuantity() <= 0) {
                log.error("{} quantity invalid: {}", prefix, item.getQuantity());
                throw new StripeProviderException(
                        ErrorCodeEnum.INVALID_QUANTITY.getErrorCode(),
                        ErrorCodeEnum.INVALID_QUANTITY.getErrorMessage() + " at index " + i,
                        HttpStatus.BAD_REQUEST
                        );
            }

            i++;
        }
        // All validations passed
        log.debug("CreatePaymentReq validation passed");
    }
}
