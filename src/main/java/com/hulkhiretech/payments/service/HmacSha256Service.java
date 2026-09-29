package com.hulkhiretech.payments.service;

public interface HmacSha256Service {
    void isHmacSignatureValid(String payload, String signature);
}