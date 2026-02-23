package com.hulkhiretech.payments.service.impl;

import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.service.helper.CreatePaymentHelper;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final HttpServiceEngine httpServiceEngine;
	private final CreatePaymentHelper createPaymentHelper;

	@Override
	public String createPayment() {
		log.info("Processing payment...");

		HttpRequest httpRequest = createPaymentHelper.prepareStripeCreateSessionRequest();

		String httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("Received response from HttpServiceEngine: {}", httpResponse);
		return httpResponse;
	}

	
}
