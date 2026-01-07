package com.logistechpro.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalesOrderLineResponse {
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
