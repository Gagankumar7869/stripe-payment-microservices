package com.hulkhiretech.payments.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
}

