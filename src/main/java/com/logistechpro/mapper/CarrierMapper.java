package com.logistechpro.mapper;

import com.logistechpro.dto.Request.CarrierRequest;
import com.logistechpro.dto.Response.CarrierResponse;
import com.logistechpro.models.Carrier;
import org.mapstruct.Mapper;
import org.mapstruct.Builder;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CarrierMapper {
    Carrier toEntity(CarrierRequest carrier);
    CarrierResponse toResponse(Carrier carrier);
}
