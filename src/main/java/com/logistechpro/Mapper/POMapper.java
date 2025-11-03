package com.logistechpro.Mapper;

import com.logistechpro.DTO.Response.POLineResponse;
import com.logistechpro.DTO.Response.POResponse;
import com.logistechpro.Models.PurchaseOrder;
import com.logistechpro.Models.PurchaseOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface POMapper {

    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "lines", target = "lines")
    POResponse toResponse(PurchaseOrder po);

    @Mapping(source = "product.name", target = "productName")
    POLineResponse toLineResponse(PurchaseOrderLine line);
}

