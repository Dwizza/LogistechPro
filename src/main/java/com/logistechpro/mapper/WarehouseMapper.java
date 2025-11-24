package com.logistechpro.mapper;

import com.logistechpro.dto.Request.WarehouseRequest;
import com.logistechpro.dto.Response.WarehouseResponse;
import com.logistechpro.models.Warehouse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Warehouse toEntity(WarehouseRequest request);
    WarehouseResponse toResponse(Warehouse warehouse);
}
