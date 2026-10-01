package com.gagan.payments.pojo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "CreatePaymentReq", description = "Request payload to create a payment session. Contains redirect URLs and the list of line items to charge.")
public class CreatePaymentReq {
	@Schema(description = "URL where the user will be redirected after successful payment", example = "https://example.com/success")
	private String successUrl;

	@Schema(description = "URL where the user will be redirected if they cancel the payment", example = "https://example.com/cancel")
	private String cancelUrl;
	
	@Schema(description = "List of items included in the payment", implementation = LineItem.class)
	List<LineItem> lineItems;
	

}