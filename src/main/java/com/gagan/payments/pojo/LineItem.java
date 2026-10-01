package com.gagan.payments.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "LineItem", description = "An individual line item to be charged in the payment session")
public class LineItem {
	@Schema(description = "Product name or description", example = "T-shirt")
	private String productName;
	
	@Schema(description = "3-letter currency code (ISO 4217)", example = "USD")
	private String currency;
	
	@Schema(description = "Unit amount in the smallest currency unit (e.g., cents)", example = "500")
	private int unitAmount;
	
	@Schema(description = "Quantity of the item to charge", example = "2")
	private int quantity;

}