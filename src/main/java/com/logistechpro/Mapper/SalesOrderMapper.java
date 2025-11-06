package com.logistechpro.Mapper;

import com.logistechpro.DTO.*;
import com.logistechpro.DTO.Request.SalesOrderLineRequest;
import com.logistechpro.DTO.Response.SalesOrderLineResponse;
import com.logistechpro.DTO.Response.SalesOrderResponse;
import com.logistechpro.DTO.SalesOrderRequest;
import com.logistechpro.Models.*;
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
