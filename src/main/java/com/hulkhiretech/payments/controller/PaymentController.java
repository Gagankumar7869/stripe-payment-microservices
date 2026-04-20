package com.hulkhiretech.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.pojo.CreatePaymentReq;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;


	@Operation(summary = "Create a new payment session",
			description = "Creates a Stripe checkout session using the provided payment details and returns a hosted page URL and session id.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Payment session created successfully", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
					@ApiResponse(responseCode = "400", description = "Invalid request payload"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
	}
			)
	@PostMapping
	public PaymentResponse createPayment(@io.swagger.v3.oas.annotations.parameters
			.RequestBody(description = "CreatePaymentReq containing success/cancel URLs and the list of line items to charge")
	@RequestBody CreatePaymentReq createPaymentReq) {

		log.info("Received request to create payment createPaymentReq: {}", createPaymentReq);

		// Simulate payment processing logic here

		PaymentResponse paymentResponse= paymentService.createPayment(createPaymentReq);

		log.info("Payment processing completed with response: {}", paymentResponse);
		return  paymentResponse;
	}


}