package com.logistechpro.DTO.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesOrderLineRequest {

    @NotNull
    private Long productId;

    @Positive
    private Integer quantity;

    @Positive
    private BigDecimal unitPrice;
}
