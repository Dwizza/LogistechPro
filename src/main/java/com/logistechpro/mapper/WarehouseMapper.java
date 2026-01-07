package com.logistechpro.mapper;

import com.logistechpro.dto.request.WarehouseRequest;
import com.logistechpro.dto.response.WarehouseResponse;
import com.logistechpro.models.Warehouse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Warehouse toEntity(WarehouseRequest request);
    WarehouseResponse toResponse(Warehouse warehouse);
}
