package com.hulkhiretech.payments.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {
	
	private final RestClient restClient;
	
	
	public String makeHttpCall() {
		log.info("Making API call to Stripe...");
		/*
		 * 
		 *  How to call api using restclient
		 *  we need headers
		 *  we need basic auth
		 *  we need to use headers that is HttpHeaders class in springboot
		 *  
		 *  */
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBasicAuth("sk_test_51Sx6HFC2d8oLw4onOKYYzb0EOsV4IAfeBI7qrqsdScGkdIy65j4"
				+ "rIGLHwcqhUSyDi6YdxwDyNWweHAcb8lQhTRpN00blC2GxK0", "");
		httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
		
		
		//Form body
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

		formData.add("line_items[0][quantity]", "2");
		formData.add("mode", "payment");
		formData.add("success_url", "https://example.com/success");
		formData.add("line_items[0][price_data][currency]", "eur");
		formData.add("line_items[0][price_data][product_data][name]", "Phone");
		formData.add("line_items[0][price_data][unit_amount]", "10000");
		
		
		ResponseEntity<String> httpResponse=restClient.method(HttpMethod.POST)
		          .uri("https://api.stripe.com/v1/checkout/sessions")
		          .headers( restClientHeaders->restClientHeaders.addAll(httpHeaders))
		          .body(formData)
		          .retrieve()
		          .toEntity(String.class);
		
		
		
		    log.info("Received response from Stripe API - Status Code: {}, Body: {}",
		    		httpResponse.getStatusCode(), httpResponse.getBody());
                  
		          
		
		return "\n"+httpResponse.getBody();
	}
	
	
	
	@PostConstruct
	public void init() {
		log.info("HttpServiceEngine initialized with RestClient: {}", restClient);
	}

}
