package com.logistechpro.mapper;

import com.logistechpro.dto.response.ShipmentResponse;
import com.logistechpro.models.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface ShipmentMapper {

    @Mapping(source = "carrier.id", target = "carrierId")
    @Mapping(source = "carrier.name", target = "carrierName")
    @Mapping(source = "salesOrder.id", target = "salesOrderId")
    ShipmentResponse toResponse(Shipment shipment);
}
