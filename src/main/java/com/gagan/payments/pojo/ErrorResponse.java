package com.gagan.payments.pojo;

import lombok.Data;

/**
 * Simple error response DTO used across the service.
 */
@Data
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;

}
