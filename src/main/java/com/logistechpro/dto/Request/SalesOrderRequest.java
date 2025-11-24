package com.logistechpro.dto.Request;

import com.logistechpro.dto.Request.SalesOrderLineRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SalesOrderRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private Long warehouseId;

    @Valid
    private List<SalesOrderLineRequest> lines;
}
