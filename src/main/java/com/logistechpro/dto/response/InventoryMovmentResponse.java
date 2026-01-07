package com.logistechpro.dto.response;

import com.logistechpro.models.Enums.MovementType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryMovmentResponse {
    private Long id;
    private String productSku;
    private String productName;
    private String warehouseCode;
    private String warehouseName;
    private MovementType type;
    private Integer quantity;
    private LocalDateTime occurredAt;
    private String referenceDocument;
    private String description;
}
