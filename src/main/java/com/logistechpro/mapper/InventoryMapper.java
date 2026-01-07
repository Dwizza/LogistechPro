package com.logistechpro.mapper;

import com.logistechpro.dto.response.InventoryResponse;
import com.logistechpro.models.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(target = "available", expression = "java(inventory.getAvailable())")
    InventoryResponse toResponse(Inventory inventory);
}
