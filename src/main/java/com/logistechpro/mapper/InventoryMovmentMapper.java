package com.logistechpro.mapper;

import com.logistechpro.dto.Response.InventoryMovmentResponse;
import com.logistechpro.models.InventoryMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMovmentMapper {

    @Mapping(source = "product.sku", target = "productSku")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "warehouse.code", target = "warehouseCode")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    InventoryMovmentResponse toResponse(InventoryMovement movement);
}

