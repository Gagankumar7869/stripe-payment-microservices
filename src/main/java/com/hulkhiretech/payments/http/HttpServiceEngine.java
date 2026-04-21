package com.hulkhiretech.payments.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {

	private final RestClient restClient;


	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("Making API call to Stripe...");

		try {
			ResponseEntity<String> httpResponse=restClient.method(httpRequest.getHttpMethod())
					.uri(httpRequest.getUrl())
					.headers( restClientHeaders->restClientHeaders.addAll(httpRequest.getHttpHeaders()) )
					.body(httpRequest.getRequestData())
					.retrieve()
					.toEntity(String.class);

			log.info("Received response from Stripe API - Status Code: {}, Body: {}",
					httpResponse.getStatusCode(), httpResponse.getBody());

			return httpResponse;

		} catch(HttpClientErrorException | HttpServerErrorException e) {
			log.error("HTTP error occurred while calling Stripe API: Status Code: {}, Response Body: {}",
					e.getStatusCode(), e.getResponseBodyAsString(), e);

			//If we get 503 or 504 then throw StripeProviderException 
			if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE
					|| e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {

				log.error("Stripe API is currently unavailable ({}). Throwing StripeProviderException.", e.getStatusCode());
				throw new StripeProviderException(ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorCode(),
						ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR
						);
			}

			//Prepare ResponseEntity from the exception details and return to the caller
			ResponseEntity<String> errorResponse= ResponseEntity
					.status(e.getStatusCode())
					.headers(e.getResponseHeaders())
					.body(e.getResponseBodyAsString());

			return errorResponse;

		} catch (Exception e) {
			log.error("Error occurred while calling Stripe API: ", e);
			// Return a generic error response with HTTP 500 status

			throw new StripeProviderException(
					ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorCode(),
					ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostConstruct
	public void init() {
		log.info("HttpServiceEngine initialized with RestClient: {}", restClient);
	}





}
