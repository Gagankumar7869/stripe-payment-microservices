package com.hulkhiretech.payments.constant;

public class Constant {
 

	public static final String CREATE_SESSION_SUCCESS_URL = "success_url";

	public static final String CREATE_SESSION_MODE = "mode";
	public static final String CREATE_SESSION_MODE_PAYMENT = "payment";

	public static final String CREATE_SESSION_CANCEL_URL = "cancel_url";
	public static final String LINE_ITEMS = "line_items";
    public static final String QUANTITY = "[quantity]";
    public static final String PRICE_DATA_CURRENCY = "[price_data][currency]";
    public static final String PRICE_DATA_UNIT_AMOUNT = "[price_data][unit_amount]";
    public static final String PRICE_DATA_PRODUCT_NAME = "[price_data][product_data][name]";
	
	private Constant() {
		  /* This utility class should not be instantiated */
		 }

}
