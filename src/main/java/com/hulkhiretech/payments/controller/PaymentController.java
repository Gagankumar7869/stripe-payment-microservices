package com.hulkhiretech.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.pojo.PaymentRequest;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
public class PaymentController {
	
	@PostMapping
	public String createPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
		log.info("Received request to create payment... paymentRequest: {}", paymentRequest);
		return "Payment created successfully! \n" + paymentRequest;
	}

}
