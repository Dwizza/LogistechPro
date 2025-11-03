package com.logistechpro.DTO.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class POLineRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    @Positive(message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
}
