package com.gagan.payments.service.helper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gagan.payments.constant.ErrorCodeEnum;
import com.gagan.payments.exception.PaymentValidationException;
import com.gagan.payments.http.HttpRequest;
import com.gagan.payments.pojo.LineItem;
import com.gagan.payments.pojo.PaymentRequest;
import com.gagan.payments.stripeprovider.SPCreatePaymentReq;
import com.gagan.payments.stripeprovider.SPErrorResponse;
import com.gagan.payments.stripeprovider.SPPaymentResponse;
import com.gagan.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeProviderHelper {
	
	private final JsonUtil jsonUtil;
	
	@Value("${stripe.provider.createPaymentUrl}")
	private String createStripeProviderPaymentUrl; 

	public HttpRequest createHttpRequest(PaymentRequest paymentRequest) {
		SPCreatePaymentReq spReq = new SPCreatePaymentReq();
		spReq.setSuccessUrl(paymentRequest.getPayment().getSuccessUrl());
		spReq.setCancelUrl(paymentRequest.getPayment().getCancelUrl());

		if (paymentRequest.getPayment().getLineItems() != null && !paymentRequest.getPayment().getLineItems().isEmpty()) {
		    List<com.gagan.payments.stripeprovider.LineItem> spLineItems = paymentRequest.getPayment().getLineItems().stream().map(li -> {
		            com.gagan.payments.stripeprovider.LineItem item = new com.gagan.payments.stripeprovider.LineItem();
		            item.setCurrency(li.getCurrency());
		            item.setProductName(li.getProductName());
		            item.setUnitAmount(li.getUnitAmount() == null ? 0 : li.getUnitAmount());
		            item.setQuantity(li.getQuantity() == null ? 0 : li.getQuantity());
		            return item;
		        }).collect(Collectors.toList());
		    spReq.setLineItems(spLineItems);
		}
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpHeaders(new HttpHeaders());
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(createStripeProviderPaymentUrl);
		httpRequest.setRequestData(spReq);
		return httpRequest;
	}

	public SPPaymentResponse processResponse(ResponseEntity<String> httpResponse) {
		if (httpResponse.getStatusCode().is2xxSuccessful()) {
			SPPaymentResponse paymentResponse = jsonUtil.convertJsonToObject(httpResponse.getBody(), SPPaymentResponse.class);
			if (paymentResponse != null && paymentResponse.getHostedPageUrl() != null) {
				return paymentResponse;
			} 
		}
		
		if (httpResponse.getStatusCode().is4xxClientError() || httpResponse.getStatusCode().is5xxServerError()) {
			SPErrorResponse stripeError = jsonUtil.convertJsonToObject(httpResponse.getBody(), SPErrorResponse.class);
			if (stripeError != null) {
				throw new PaymentValidationException(
						stripeError.getErrorCode(),
						stripeError.getErrorMessage(),
						HttpStatus.valueOf(httpResponse.getStatusCode().value()));
			}
		}
		
		throw new PaymentValidationException(
				ErrorCodeEnum.INVALID_STRIPE_PROVIDER_RESPONSE.getErrorCode(),
				ErrorCodeEnum.INVALID_STRIPE_PROVIDER_RESPONSE.getErrorMessage(),
				HttpStatus.BAD_GATEWAY);
	}
}