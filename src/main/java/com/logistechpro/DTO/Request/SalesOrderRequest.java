package com.logistechpro.DTO;

import com.logistechpro.DTO.Request.SalesOrderLineRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalesOrderRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private Long warehouseId;

    @Valid
    private List<SalesOrderLineRequest> lines;
}
