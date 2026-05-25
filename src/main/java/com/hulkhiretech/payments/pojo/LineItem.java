package com.hulkhiretech.payments.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LineItem {
	@NotBlank(message = "PRODUCT_NAME_BLANK")
    @Size(max = 100, message = "PRODUCT_NAME_TOO_LONG")
    private String productName;

    @NotBlank(message = "CURRENCY_BLANK")
    @Pattern(regexp = "^[A-Z]{3}$", message = "CURRENCY_INVALID")
    private String currency;

    @NotNull(message = "UNIT_AMOUNT_NULL")
    @Positive(message = "UNIT_AMOUNT_INVALID")
    private Integer unitAmount;

    @NotNull(message = "QUANTITY_NULL")
    @Min(value = 1, message = "QUANTITY_INVALID")
    private Integer quantity;
    }