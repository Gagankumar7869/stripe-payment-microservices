package com.hulkhiretech.payments.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreatePaymentReq;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.ValidationService;
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
	private final ValidationService validationService;

	@Override
	public PaymentResponse createPayment(CreatePaymentReq createPaymentReq) {
		log.info("Processing payment...");
		// validate request fields
		validationService.isValid(createPaymentReq);



		HttpRequest httpRequest = createPaymentHelper.prepareStripeCreateSessionRequest(createPaymentReq);

		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("Received response from HttpServiceEngine: {}", httpResponse);

		CheckoutSessionResponse checkoutSessionResponse=processStripeResponse(httpResponse);
		log.info("Processed Stripe response and obtained CheckoutSessionResponse: {}", checkoutSessionResponse);

		//Note: The above method executes means its only success.
		//For error above method will throw exception


		PaymentResponse paymentResponse = mapToPaymentResponse(checkoutSessionResponse);
		log.info("Mapped to PaymentResponse: {}", paymentResponse);
		return paymentResponse;
	}

	private CheckoutSessionResponse processStripeResponse(ResponseEntity<String> httpResponse) {

		//Check if httpResponse is 2xx, then convert to checkoutSessionResponse
		if (httpResponse.getStatusCode().is2xxSuccessful()) {
			log.info("Received successful response from Stripe API: Status Code: {}, Body: {}",
					httpResponse.getStatusCode(), httpResponse.getBody());

			String body = httpResponse.getBody();
			log.debug("Processing successful response from Stripe: {}", body);


			CheckoutSessionResponse checkoutSessionResponse = jsonUtil.convertJsonToObject(body, CheckoutSessionResponse.class);

			if(checkoutSessionResponse!=null && checkoutSessionResponse.getUrl()!=null) {
				log.info("Stripe Checkout session created successfully with  sessionId: {} and url: {}", checkoutSessionResponse.getId(), checkoutSessionResponse.getUrl());
				//In the below line we are returning CheckoutSessionResponse obj
				//SUCCESS
				return checkoutSessionResponse;
			} 

			log.error("Stripe API call returned 2xx status but response body is invalid or missing sesion url. Status Code: {}, Body: {}", httpResponse.getStatusCode(), httpResponse.getBody());

		}
		throw new RuntimeException("Failed to create Stripe Checkout session. HTTP Status: " + httpResponse.getStatusCode() + ", Response Body: " + httpResponse.getBody());

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