package com.gagan.payments.stripe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "CheckoutSessionResponse", description = "Partial Stripe Checkout Session response used by the application")
public class CheckoutSessionResponse {

    @Schema(description = "Stripe session id", example = "cs_test_a1b2c3d4")
    private String id;

    @Schema(description = "URL of the hosted Stripe checkout page", example = "https://checkout.stripe.com/pay/cs_test_a1b2c3d4")
    private String url;

    @Schema(description = "Session status", example = "open")
    private String status;

    @JsonProperty("payment_status")
    @Schema(description = "Payment status for the session", example = "paid")
    private String paymentStatus;
}