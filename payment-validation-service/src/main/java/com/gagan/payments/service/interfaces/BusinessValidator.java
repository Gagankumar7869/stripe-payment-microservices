package com.gagan.payments.service.interfaces;

import com.gagan.payments.pojo.PaymentRequest;

public interface BusinessValidator {
	public void validate(PaymentRequest paymentRequest);
}