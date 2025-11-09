package com.logistechpro.Mapper;

import com.logistechpro.DTO.Response.ShipmentResponse;
import com.logistechpro.Models.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface ShipmentMapper {

    @Mapping(source = "carrier.id", target = "carrierId")
    @Mapping(source = "carrier.name", target = "carrierName")
    @Mapping(source = "salesOrder.id", target = "salesOrderId")
    ShipmentResponse toResponse(Shipment shipment);
}
