package com.gagan.payments.service.interfaces;

import com.gagan.payments.pojo.PaymentRequest;
import com.gagan.payments.pojo.PaymentResponse;

public interface PaymentService {
    
    PaymentResponse validateAndCreatePayment(PaymentRequest paymentRequest);
    
    String getPaymentDetails(String merchantTxnRef);
}