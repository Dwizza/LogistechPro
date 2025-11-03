package com.logistechpro.DTO.Response;

import com.logistechpro.Models.Enums.PurchaseOrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class POResponse {
    private Long id;
    private String supplierName;
    private String warehouseName;
    private String status;
    private LocalDateTime createdAt;
    private List<POLineResponse> lines;
}
