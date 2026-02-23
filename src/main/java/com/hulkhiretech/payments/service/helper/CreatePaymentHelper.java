package com.hulkhiretech.payments.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.http.HttpRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreatePaymentHelper {
	
	

	@Value("${stripe.api.key}")
	private String stripeApiKey ;
	
	@Value("${stripe.create.session.url}")
	private String stripeCreateSessionUrl ;
	
	public HttpRequest prepareStripeCreateSessionRequest() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBasicAuth(stripeApiKey, "");
				
		httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

		formData.add(Constant.CREATE_SESSION_MODE, "payment");
		formData.add(Constant.CREATE_SESSION_SUCCESS_URL, "https://example.com/success");
		
		formData.add("line_items[0][quantity]", "2");
		formData.add("line_items[0][price_data][currency]", "eur");
		formData.add("line_items[0][price_data][product_data][name]", "Phone");
		formData.add("line_items[0][price_data][unit_amount]", "10000");

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(stripeCreateSessionUrl);
		httpRequest.setHttpHeaders(httpHeaders);
		httpRequest.setRequestData(formData);
		return httpRequest;
	}


}
