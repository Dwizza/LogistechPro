package com.logistechpro.mapper;

import com.logistechpro.dto.Request.SupplierRequest;
import com.logistechpro.dto.Response.SupplierResponse;
import com.logistechpro.models.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toEntity(SupplierRequest request);

    SupplierResponse toResponse(Supplier supplier);

    void updateEntityFromDto(SupplierRequest request, @MappingTarget Supplier supplier);
}

