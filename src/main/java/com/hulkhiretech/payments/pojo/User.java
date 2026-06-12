package com.hulkhiretech.payments.pojo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class User {

    @NotBlank(message = "END_USER_ID_BLANK")
    @Size(max = 100, message = "END_USER_ID_INVALID")
    private String endUserID;

    @NotBlank(message = "FIRST_NAME_BLANK")
    @Size(max = 50, message = "FIRST_NAME_INVALID")
    private String firstname;

    @NotBlank(message = "LAST_NAME_BLANK")
    @Size(max = 50, message = "LAST_NAME_INVALID")
    private String lastname;

    @NotBlank(message = "EMAIL_BLANK")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "MOBILE_PHONE_BLANK")
    @Pattern(
        regexp = "^\\+?[1-9]\\d{7,14}$",
        message = "MOBILE_PHONE_INVALID"
    )
    private String mobilePhone;
}