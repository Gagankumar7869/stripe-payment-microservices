package com.hulkhiretech.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.pojo.CreatePaymentReq;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;


	@PostMapping
	public String createPayment(@RequestBody CreatePaymentReq createPaymentReq) {

		log.info("Received request to create payment createPaymentReq: {}", createPaymentReq);

		// Simulate payment processing logic here

		String response= paymentService.createPayment(createPaymentReq);

		log.info("Payment processing completed with response: {}", response);
		return "Payment created successfully! Response: " + response;
	}


}
