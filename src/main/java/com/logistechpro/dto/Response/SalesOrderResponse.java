package com.logistechpro.dto.Response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SalesOrderResponse {
    private Long id;
    private String clientName;
    private String warehouseName;
    private String status;
    private LocalDateTime createdAt;
    private List<SalesOrderLineResponse> lines;
}
