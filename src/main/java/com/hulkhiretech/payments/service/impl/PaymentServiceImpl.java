package com.hulkhiretech.payments.service.impl;

import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	/*
	 * Make Rest Api call to Stripe create-session api
	 * Prepare the request to call stripe
	 * url,post,formencoded request body,basic auth header
	 * what springboot library available to make rest api call
	 * how to use RestClient to make rest api call to stripe
	 * handle the response from stripe for both success and failure case
	*/
	
	private final HttpServiceEngine httpServiceEngine;
    @Override
	public String createPayment() {
    	        log.info("Processing payment...");
    	        String httpResponse = httpServiceEngine.makeHttpCall();
    	        log.info("Received response from HttpServiceEngine: {}", httpResponse);
		return httpResponse;
	}

}
