package com.gagan.payments.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.gagan.payments.constant.ErrorCodeEnum;
import com.gagan.payments.exception.PaymentValidationException;

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
		log.info("Making HTTP call to external service: {}", httpRequest.getUrl());

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
			log.error("HTTP client/server error occurred. Status code: {}", ex.getStatusCode());

			// Let downstream outages propagate so Resilience4j tracks them
			if (ex.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE || ex.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
				throw ex;
			}
			return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
		}
		// Notice: No generic catch (Exception ex) block here.
		// Connection/timeout exceptions (ResourceAccessException, SocketException)
		// must escape makeHttpCall so @CircuitBreaker intercepts them and invokes fallbackProcessPayment.
	}

	public ResponseEntity<String> fallbackProcessPayment(HttpRequest httpRequest, Throwable t) {
		log.error("Resilience4j Circuit Breaker fallback triggered! Downstream service unreachable: {}", t.getMessage());

		throw new PaymentValidationException(
				ErrorCodeEnum.ERROR_CONNECTING_TO_EXTERNAL_SERVICE.getErrorCode(),
				"External payment service is temporarily unavailable. Please try again later.",
				HttpStatus.SERVICE_UNAVAILABLE);
	}

	@PostConstruct
	public void init() {
		log.info("Initializing HttpServiceEngine... restClient: {}", restClient);
	}
}