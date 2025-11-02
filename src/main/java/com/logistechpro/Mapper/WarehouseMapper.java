package com.logistechpro.Mapper;

import com.logistechpro.DTO.Request.WarehouseRequest;
import com.logistechpro.DTO.Response.WarehouseResponse;
import com.logistechpro.Models.Warehouse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Warehouse toEntity(WarehouseRequest request);
    WarehouseResponse toResponse(Warehouse warehouse);
}
