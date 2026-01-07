package com.logistechpro.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class POLineResponse {
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
