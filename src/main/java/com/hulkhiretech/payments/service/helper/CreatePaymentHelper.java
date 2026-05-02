package com.hulkhiretech.payments.service.helper;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.pojo.CreatePaymentReq;
import com.hulkhiretech.payments.pojo.LineItem;
import com.hulkhiretech.payments.stripe.CheckoutSessionResponse;
import com.hulkhiretech.payments.stripe.StripeError;
import com.hulkhiretech.payments.stripe.StripeErrorResponse;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatePaymentHelper {
	
	

	@Value("${stripe.api.key}")
	private String stripeApiKey ;
	
	@Value("${stripe.create.session.url}")
	private String stripeCreateSessionUrl ;
	
	private final JsonUtil jsonUtil;
	
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
	public CheckoutSessionResponse processStripeResponse(ResponseEntity<String> httpResponse) {

		//Check if httpResponse is 2xx, then convert to checkoutSessionResponse
		if (httpResponse.getStatusCode().is2xxSuccessful()) {
			log.info("Received successful response from Stripe API: Status Code: {}, Body: {}",
					httpResponse.getStatusCode(), httpResponse.getBody());

			String body = httpResponse.getBody();
			log.debug("Processing successful response from Stripe: {}", body);


			CheckoutSessionResponse checkoutSessionResponse = jsonUtil.convertJsonToObject(body, CheckoutSessionResponse.class);

			if(checkoutSessionResponse!=null && checkoutSessionResponse.getUrl()!=null) {
				log.info("Stripe Checkout session created successfully with  sessionId: {} and url: {}",
						checkoutSessionResponse.getId(), checkoutSessionResponse.getUrl());
				//In the below line we are returning CheckoutSessionResponse obj
				//SUCCESS
				return checkoutSessionResponse;
			} 

			log.error("Stripe API call returned 2xx status but response body is invalid or missing sesion url."
					+ " Status Code: {}, Body: {}", httpResponse.getStatusCode(), httpResponse.getBody());

		}
		if (httpResponse.getStatusCode().is4xxClientError() || httpResponse.getStatusCode().is5xxServerError()) {
			log.error("Received error response from Stripe API: Status Code: {}, Body: {}",
					httpResponse.getStatusCode(), httpResponse.getBody());

			//use jsonutil to convert the error response body to StripeErrorResponse object
			StripeErrorResponse stripeError = jsonUtil.convertJsonToObject(httpResponse.getBody(),
					StripeErrorResponse.class);
			if (stripeError != null && stripeError.getError() != null) {
				log.error("Stripe API error details: Code: {}, Message: {}, Type: {}, Param: {}",
						stripeError.getError().getCode(), stripeError.getError().getMessage(),
						stripeError.getError().getType(), stripeError.getError().getParam());
				
				String stripeConcatenatedErrorMessage = prepareStripeErrorMessage(stripeError);
				log.error("Concatenated Stripe error message: {}", stripeConcatenatedErrorMessage);
				
				throw new StripeProviderException(
						ErrorCodeEnum.STRIPE_API_ERROR.getErrorCode(),
						stripeConcatenatedErrorMessage,
						HttpStatus.valueOf(httpResponse.getStatusCode().value()));

			} 
			log.error("Stripe API call failed with error status but response body is invalid or missing error details."
					+ " Status Code: {}, Body: {}", httpResponse.getStatusCode(), httpResponse.getBody());
			
		}

		throw new StripeProviderException(ErrorCodeEnum.INVALID_STRIPE_RESPONSE.getErrorCode(),
				ErrorCodeEnum.INVALID_STRIPE_RESPONSE.getErrorMessage(),
				HttpStatus.BAD_GATEWAY);

	}
	
	private String prepareStripeErrorMessage(StripeErrorResponse stripeErrorResponse) {

	   

	    StripeError error = stripeErrorResponse.getError();

	    return Stream.of(
	            error.getType(),     // always present
	            error.getMessage(),
	            error.getParam(),
	            error.getCode()
	    )
	    .filter(Objects::nonNull)
	    .map(String::trim)
	    .filter(s -> !s.isEmpty())
	    .collect(Collectors.joining(" | "));
	}



}
