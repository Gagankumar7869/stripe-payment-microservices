package com.gagan.payments.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gagan.payments.pojo.PaymentRequest;
import com.gagan.payments.pojo.PaymentResponse;
import com.gagan.payments.service.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@Value("${mytestkey}")
	private String myTestKey;

	@PostMapping
	public PaymentResponse createPayment(
			@Valid @RequestBody PaymentRequest paymentRequest) {
		log.info("Creating payment... paymentRequest: {}", paymentRequest);

		PaymentResponse serviceResponse = paymentService.validateAndCreatePayment(paymentRequest);
		log.info("Payment created: {}", serviceResponse);

		return serviceResponse;
	}

	@GetMapping("/{merchantTxnRef}")
	public ResponseEntity<String> getPaymentStatus(@PathVariable String merchantTxnRef) {  
		log.info("Getting payment status for txnRef: {}", merchantTxnRef);

		String savedRequestJson = paymentService.getPaymentDetails(merchantTxnRef);

		return ResponseEntity.ok(savedRequestJson);
	}

	@PostConstruct
	public void init() {   
		log.info("****myTestKey value: {}", myTestKey);  
	}
}