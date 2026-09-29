package com.hulkhiretech.payments.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaymentValidationException;
import com.hulkhiretech.payments.service.HmacSha256Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HmacSha256ServiceImpl implements HmacSha256Service {

    @Value("${hmac.secret.key}")
    private String secretKey;

    @Override
    public void isHmacSignatureValid(String payload, String signature) {
        log.debug("Validating HMAC signature...");
        
        if (signature == null || signature.isBlank()) {
            log.error("Missing HMAC signature header");
            throw new PaymentValidationException(
                    ErrorCodeEnum.MISSING_HMAC.getErrorCode(),
                    ErrorCodeEnum.MISSING_HMAC.getErrorMessage(),
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            Mac sha256_HMAC = Mac.getInstance(Constant.HMAC_SHA256);
            SecretKeySpec secret_key = new SecretKeySpec(
            		secretKey.getBytes(StandardCharsets.UTF_8), 
            		Constant.HMAC_SHA256);
            
            sha256_HMAC.init(secret_key);

            byte[] hashBytes = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = Base64.getEncoder().encodeToString(hashBytes);

            if (!calculatedSignature.equals(signature)) {
                log.error("Invalid HMAC signature. Expected: {}, Received: {}", calculatedSignature, signature);
                throw new PaymentValidationException(
                        ErrorCodeEnum.INVALID_HMAC.getErrorCode(),
                        ErrorCodeEnum.INVALID_HMAC.getErrorMessage(),
                        HttpStatus.UNAUTHORIZED);
            }
            
            log.info("HMAC signature successfully verified.");

        } catch (PaymentValidationException ex) {
            throw ex; 
        } catch (Exception ex) {
            log.error("Error computing HMAC signature: {}", ex.getMessage(), ex);
            throw new PaymentValidationException(
                    ErrorCodeEnum.HMAC_COMPUTATION_ERROR.getErrorCode(),
                    ErrorCodeEnum.HMAC_COMPUTATION_ERROR.getErrorMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}