package com.logistechpro.DTO.Response;

import com.logistechpro.Models.Enums.ShipmentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShipmentResponse {
    private Long id;
    private Long salesOrderId;
    private Long carrierId;
    private String carrierName;
    private String trackingNumber;
    private ShipmentStatus status;
    private LocalDateTime plannedDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
}
