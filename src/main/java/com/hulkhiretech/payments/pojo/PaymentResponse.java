package com.hulkhiretech.payments.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "PaymentResponse", description = "Internal response returned after creating a payment session. Contains Stripe session id and the hosted page URL.")
public class PaymentResponse {
	@Schema(description = "Stripe Checkout Session ID", example = "cs_test_a1b2c3d4")
	private String stripeSessionId;
	@Schema(description = "URL of the hosted Stripe checkout page for the session", example = "https://checkout.stripe.com/pay/cs_test_a1b2c3d4")
	private String hostedPageUrl;

}