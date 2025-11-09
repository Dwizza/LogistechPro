package com.logistechpro.Mapper;

import com.logistechpro.DTO.Request.CarrierRequest;
import com.logistechpro.DTO.Response.CarrierResponse;
import com.logistechpro.Models.Carrier;
import org.mapstruct.Mapper;
import org.mapstruct.Builder;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CarrierMapper {
    Carrier toEntity(CarrierRequest carrier);
    CarrierResponse toResponse(Carrier carrier);
}
