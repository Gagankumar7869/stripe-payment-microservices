package com.gagan.payments.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gagan.payments.http.HttpRequest;
import com.gagan.payments.http.HttpServiceEngine;
import com.gagan.payments.pojo.CreatePaymentReq;
import com.gagan.payments.pojo.PaymentResponse;
import com.gagan.payments.service.ValidationService;
import com.gagan.payments.service.helper.CreatePaymentHelper;
import com.gagan.payments.service.interfaces.PaymentService;
import com.gagan.payments.stripe.CheckoutSessionResponse;
import com.gagan.payments.util.JsonUtil;

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

	/**
	 * Creates a payment by initiating a Stripe Checkout Session.
	 *
	 * Flow:
	 * 1. Validates the incoming payment request.
	 * 2. Prepares the HTTP request for Stripe API.
	 * 3. Calls the external HTTP service to create a checkout session.
	 * 4. Processes the Stripe response and converts it into a CheckoutSessionResponse.
	 * 5. Maps the CheckoutSessionResponse to the internal PaymentResponse object.
	 *
	 * 
	 * - If the Stripe API returns an error, an exception is thrown during response processing.
	 * - Successful execution of processStripeResponse() implies a valid Stripe response.
	 *
	 * @param createPaymentReq the request containing payment details
	 * @return PaymentResponse containing the checkout/session details
	 */

	@Override
	public PaymentResponse createPayment(CreatePaymentReq createPaymentReq) {
		log.info("Processing payment...");
		// validate request fields
		validationService.isValid(createPaymentReq);



		HttpRequest httpRequest = createPaymentHelper.prepareStripeCreateSessionRequest(createPaymentReq);

		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("Received response from HttpServiceEngine: {}", httpResponse);

		CheckoutSessionResponse checkoutSessionResponse = createPaymentHelper.processStripeResponse(httpResponse);

		checkoutSessionResponse=createPaymentHelper.processStripeResponse(httpResponse);
		log.info("Processed Stripe response and obtained CheckoutSessionResponse: {}", checkoutSessionResponse);

		// The above method executes means its only success.
		//For error above method will throw exception

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