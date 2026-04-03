package com.hulkhiretech.payments.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.exception.StripeProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreatePaymentReq;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.helper.CreatePaymentHelper;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import com.hulkhiretech.payments.stripe.CheckoutSessionResponse;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final HttpServiceEngine httpServiceEngine;
	private final CreatePaymentHelper createPaymentHelper;
	private final JsonUtil jsonUtil;

	@Override
	public PaymentResponse createPayment(CreatePaymentReq createPaymentReq) {
		log.info("Processing payment...");
		
		if (createPaymentReq.getSuccessUrl() == null || createPaymentReq.getSuccessUrl().isEmpty()) {
			log.error("Missing successUrl in CreatePaymentReq");
			throw new StripeProviderException(
					"30001",
					"Missing required field: successUrl",
					HttpStatus.BAD_REQUEST);
		}

		HttpRequest httpRequest = createPaymentHelper.prepareStripeCreateSessionRequest(createPaymentReq);

		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("Received response from HttpServiceEngine: {}", httpResponse);

		String body = httpResponse.getBody();
		CheckoutSessionResponse checkoutSessionResponse = jsonUtil.convertJsonToObject(body, CheckoutSessionResponse.class);
		log.info("Parsed CheckoutSessionResponse: {}", checkoutSessionResponse);
		PaymentResponse paymentResponse = mapToPaymentResponse(checkoutSessionResponse);
		log.info("Mapped to PaymentResponse: {}", paymentResponse);
		return paymentResponse;
	}




	/**
	 * Write a map method to take CheckoutSessionResponse
	 * and convert it to PaymentResponse which is
	 *  our internal response object. This way we are not
	 */
	public PaymentResponse mapToPaymentResponse(CheckoutSessionResponse checkoutSessionResponse) {
		if (checkoutSessionResponse == null) {
			log.warn("mapToPaymentResponse called with null input");
			return null;
		}
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setStripeSessionId(checkoutSessionResponse.getId());
		paymentResponse.setHostedPageUrl(checkoutSessionResponse.getUrl());
		log.debug("Mapped CheckoutSessionResponse to PaymentResponse: {}", paymentResponse);
		return paymentResponse;
	}










}