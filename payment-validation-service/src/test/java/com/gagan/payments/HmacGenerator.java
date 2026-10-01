package com.gagan.payments;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacGenerator {
    public static void main(String[] args) throws Exception {
        String secretKey = "THIS_IS_MY_SECRET";
        
        
        String jsonInput = "{\"user\":{\"endUserID\":\"user123123\",\"firstname\":\"Gagan\",\"lastname\":\"Doe\",\"email\":\"john.doe@example.com\",\"mobilePhone\":\"+91 2345678901\"},\"payment\":{\"currency\":\"INR\",\"amount\":120000,\"brandName\":\"MyShop\",\"locale\":\"en-US\",\"country\":\"US\",\"merchantTxnRef\":\"TXN-TEST-009\",\"paymentMethod\":\"APM\",\"paymentType\":\"SALE\",\"successUrl\":\"https://example.com/success\",\"cancelUrl\":\"https://example.com/cancel\",\"lineItems\":[{\"currency\":\"INR\",\"productName\":\"Phone\",\"unitAmount\":20000,\"quantity\":1},{\"currency\":\"INR\",\"productName\":\"Headphones\",\"unitAmount\":50000,\"quantity\":2}]},\"provider\":\"STRIPE\",\"providerData\":{\"stripeCustomerId\":\"cus_12345\"}}";

        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        
        byte[] signatureBytes = mac.doFinal(jsonInput.getBytes(StandardCharsets.UTF_8));
        String hmacSignature = Base64.getEncoder().encodeToString(signatureBytes);
        
        System.out.println("Copy this into your Postman 'Hmac-Signature' Header:");
        System.out.println(hmacSignature);
    }
}