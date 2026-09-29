package com.hulkhiretech.payments.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaymentValidationException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {

	private final RestClient restClient;

	@CircuitBreaker(name = "payment-validation-service", fallbackMethod = "fallbackProcessPayment")
	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("Making HTTP call to external service...");
		try {
			ResponseEntity<String> httpResponse = restClient
					.method(httpRequest.getHttpMethod())
					.uri(httpRequest.getUrl())
					.headers(restClientHeaders -> restClientHeaders.addAll(httpRequest.getHttpHeaders()))
					.body(httpRequest.getRequestData())
					.retrieve()
					.toEntity(String.class);

			log.info("HTTP call completed. Status code: {}", httpResponse.getStatusCode());
			return httpResponse;
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			log.error("HTTP error occurred. Status code: {}", ex.getStatusCode());
			if (ex.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE || ex.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
				throw new PaymentValidationException(
						ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorCode(),
						ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
		} catch (Exception ex) {
			log.error("Error occurred while making HTTP call: ", ex);
			throw new PaymentValidationException(
					ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorCode(),
					ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> fallbackProcessPayment(HttpRequest httpRequest, Throwable t) {
		log.error("Fallback method called due to: {}", t.getMessage(), t);
		throw new PaymentValidationException(
				ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorCode(),
				ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostConstruct
	public void init() {
		log.info("Initializing HttpServiceEngine... restClient: {}", restClient);
	}
}