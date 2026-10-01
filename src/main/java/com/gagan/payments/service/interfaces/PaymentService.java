package com.gagan.payments.service.interfaces;

import com.gagan.payments.pojo.CreatePaymentReq;
import com.gagan.payments.pojo.PaymentResponse;

public interface PaymentService {
	public PaymentResponse createPayment(CreatePaymentReq createPaymentReq);

}
