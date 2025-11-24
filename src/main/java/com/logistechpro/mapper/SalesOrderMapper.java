package com.logistechpro.mapper;

import com.logistechpro.dto.Request.SalesOrderLineRequest;
import com.logistechpro.dto.Response.SalesOrderLineResponse;
import com.logistechpro.dto.Response.SalesOrderResponse;
import com.logistechpro.dto.SalesOrderRequest;
import com.logistechpro.models.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SalesOrderMapper {

    @Mapping(target = "client.id", source = "clientId")
    @Mapping(target = "warehouse.id", source = "warehouseId")
    @Mapping(target = "lines", ignore = true)
    SalesOrder toEntity(SalesOrderRequest request);

    @Mapping(target = "clientName", source = "client.user.name")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    SalesOrderResponse toResponse(SalesOrder order);

    SalesOrderLine toEntity(SalesOrderLineRequest request);
    SalesOrderLineResponse toResponse(SalesOrderLine line);
}
