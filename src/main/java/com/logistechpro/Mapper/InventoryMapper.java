package com.logistechpro.Mapper;

import com.logistechpro.DTO.Response.InventoryResponse;
import com.logistechpro.Models.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(target = "available", expression = "java(inventory.getAvailable())")
    InventoryResponse toResponse(Inventory inventory);
}
