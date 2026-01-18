package com.logistechpro.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductWithInventoryResponse {
    private Long id;
    private String sku;
    private String name;
    private String category;
    private BigDecimal avgPrice;
    private boolean active;

    private List<ProductWarehouseInventoryResponse> warehouses;
}

