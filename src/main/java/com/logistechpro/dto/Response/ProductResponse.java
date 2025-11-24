package com.logistechpro.dto.Response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String category;
    private BigDecimal avgPrice;
    private boolean active;
}
