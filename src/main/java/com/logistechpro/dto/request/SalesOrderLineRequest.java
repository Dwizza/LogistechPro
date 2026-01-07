package com.logistechpro.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SalesOrderLineRequest {

    @NotNull
    private Long productId;

    @Positive
    private Integer quantity;

    @Positive
    private BigDecimal unitPrice;
}
