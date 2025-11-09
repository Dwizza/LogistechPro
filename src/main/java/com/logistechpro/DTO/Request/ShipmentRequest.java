package com.logistechpro.DTO.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShipmentRequest {

    @NotNull(message = "SalesOrder ID is required.")
    private Long salesOrderId;

    @NotNull(message = "Carrier ID is required.")
    private Long carrierId;
}
