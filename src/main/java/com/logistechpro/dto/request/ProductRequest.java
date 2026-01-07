package com.logistechpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data

public class ProductRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    @Positive(message = "Average price must be positive")
    private BigDecimal avgPrice;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    private boolean active = true;


}
