package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class LineItem {
	private String productName;
	private String currency;
	private int unitAmount;
	private int quantity;

}
