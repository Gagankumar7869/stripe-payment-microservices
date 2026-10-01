package com.gagan.payments.service;

public interface StripeNotificationService {
    void processNotification(String stripeSignature, String jsonRequest);
}