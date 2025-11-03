package com.logistechpro.DTO.Request;

import com.logistechpro.Models.Enums.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InventoryMovmentRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Long warehouseId;
    @NotNull
    private MovementType type; // INBOUND/OUTBOUND/ADJUSTMENT
    @Positive
    @NotNull
    private Integer quantity;
    @Size(max = 255)
    private String referenceDocument;
    private String description;
}
