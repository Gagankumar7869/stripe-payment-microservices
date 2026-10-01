package com.gagan.payments.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gagan.payments.cache.ValidatorRuleCacheV2;
import com.gagan.payments.constant.ErrorCodeEnum;
import com.gagan.payments.constant.ValidatorRuleEnum;
import com.gagan.payments.entity.MerchantPaymentRequestEntity;
import com.gagan.payments.exception.PaymentValidationException;
import com.gagan.payments.http.HttpRequest;
import com.gagan.payments.http.HttpServiceEngine;
import com.gagan.payments.pojo.PaymentRequest;
import com.gagan.payments.pojo.PaymentResponse;
import com.gagan.payments.repository.interfaces.MerchantPaymentRequestRepository;
import com.gagan.payments.service.helper.StripeProviderHelper;
import com.gagan.payments.service.interfaces.BusinessValidator;
import com.gagan.payments.service.interfaces.PaymentService;
import com.gagan.payments.stripeprovider.SPPaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ApplicationContext applicationContext;
    private final ValidatorRuleCacheV2 validatorRuleCache;
    private final StripeProviderHelper stripeProviderHelper;
    private final HttpServiceEngine httpServiceEngine;
    private final MerchantPaymentRequestRepository merchantReqRepo;

    @Override
    public PaymentResponse validateAndCreatePayment(PaymentRequest paymentRequest) {
        log.info("Validating and creating payment: {} ", paymentRequest);

        List<String> validatorRules = validatorRuleCache.getValidatorRules();
        log.debug("Loaded validator rules from cache: {}", validatorRules);
        
        if (validatorRules == null || validatorRules.isEmpty()) {
            log.error("No validator rules configured, skipping validations");
            throw new PaymentValidationException(
                    ErrorCodeEnum.NO_VALIDATION_RULES_CONFIGURED.getErrorCode(),
                    ErrorCodeEnum.NO_VALIDATION_RULES_CONFIGURED.getErrorMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        for (String rule : validatorRules) {
            log.info("Applying validation rule: {}", rule);
            Optional<Class<? extends BusinessValidator>> validatorClass = ValidatorRuleEnum.getValidatorClassByRule(rule.trim());
            if(!validatorClass.isPresent()) {
                log.warn("No validator found for rule: {}", rule);
                continue;
            }

            BusinessValidator validator = applicationContext.getBean(validatorClass.get());
            if(validator == null) {
                log.warn("No bean found for validator class: {}", validatorClass.get().getName());
                continue;
            }
            validator.validate(paymentRequest);
        }

        log.info("All validations passed for payment request: {}", paymentRequest);

        HttpRequest httpRequest = stripeProviderHelper.createHttpRequest(paymentRequest);
        log.info("Prepared HttpRequest for Stripe provider: {}", httpRequest);
        
        ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
        
        SPPaymentResponse finalResponse = stripeProviderHelper.processResponse(httpResponse);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStripeSessionId(finalResponse.getStripeSessionId());
        paymentResponse.setHostedPageUrl(finalResponse.getHostedPageUrl());
        
        log.info("Final PaymentResponse to be returned: {}", paymentResponse);
        return paymentResponse;
    }

    @Override
    public String getPaymentDetails(String merchantTxnRef) {
        log.info("Fetching payment details for txnRef: {}", merchantTxnRef);
        
        MerchantPaymentRequestEntity entity = merchantReqRepo.findByMerchantTxnReference(merchantTxnRef);
        
        if (entity == null) {
            throw new PaymentValidationException(
                    ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
                    "Transaction reference not found",
                    HttpStatus.NOT_FOUND);
        }
        
        return entity.getTransactionRequest();
    }
}