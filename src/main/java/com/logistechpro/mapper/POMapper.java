package com.logistechpro.mapper;

import com.logistechpro.dto.response.POLineResponse;
import com.logistechpro.dto.response.POResponse;
import com.logistechpro.models.PurchaseOrder;
import com.logistechpro.models.PurchaseOrderLine;
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

