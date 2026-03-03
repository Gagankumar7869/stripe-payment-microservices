package com.hulkhiretech.payments.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.pojo.CreatePaymentReq;
import com.hulkhiretech.payments.pojo.LineItem;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreatePaymentHelper {
	
	

	@Value("${stripe.api.key}")
	private String stripeApiKey ;
	
	@Value("${stripe.create.session.url}")
	private String stripeCreateSessionUrl ;
	
	public HttpRequest prepareStripeCreateSessionRequest(CreatePaymentReq createPaymentReq) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBasicAuth(stripeApiKey, "");
				
		httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");


		MultiValueMap<String, String> formData = prepareFormUrlEncodedData(createPaymentReq);
		
		log.info("Prepared form data for Stripe create session request formData: {}", formData);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(stripeCreateSessionUrl);
		httpRequest.setHttpHeaders(httpHeaders);
		httpRequest.setRequestData(formData);
		return httpRequest;
	}
	public static MultiValueMap<String, String> prepareFormUrlEncodedData(
	        CreatePaymentReq request) {

	    MultiValueMap<String, String> formUrlEncodedData =
	            new LinkedMultiValueMap<>();

	    // Mandatory fields
	    formUrlEncodedData.add(Constant.CREATE_SESSION_MODE,
	            Constant.CREATE_SESSION_MODE_PAYMENT);

	    formUrlEncodedData.add(Constant.CREATE_SESSION_SUCCESS_URL,
	            request.getSuccessUrl());

	    formUrlEncodedData.add(Constant.CREATE_SESSION_CANCEL_URL,
	            request.getCancelUrl());

	    // Line items
	    if (request.getLineItems() != null
	            && !request.getLineItems().isEmpty()) {

	        for (int i = 0; i < request.getLineItems().size(); i++) {

	            LineItem item = request.getLineItems().get(i);

	            String baseKey = Constant.LINE_ITEMS + "[" + i + "]";

	            formUrlEncodedData.add(
	                baseKey + Constant.QUANTITY,
	                String.valueOf(item.getQuantity()));

	            formUrlEncodedData.add(
	                baseKey + Constant.PRICE_DATA_CURRENCY,
	                item.getCurrency());

	            formUrlEncodedData.add(
	                baseKey + Constant.PRICE_DATA_UNIT_AMOUNT,
	                String.valueOf(item.getUnitAmount()));

	            formUrlEncodedData.add(
	                baseKey + Constant.PRICE_DATA_PRODUCT_NAME,
	                item.getProductName());
	        }
	    }

	    return formUrlEncodedData;
	}


}
