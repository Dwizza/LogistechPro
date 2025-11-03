package com.logistechpro.Mapper;

import com.logistechpro.DTO.Request.SupplierRequest;
import com.logistechpro.DTO.Response.SupplierResponse;
import com.logistechpro.Models.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toEntity(SupplierRequest request);

    SupplierResponse toResponse(Supplier supplier);

    void updateEntityFromDto(SupplierRequest request, @MappingTarget Supplier supplier);
}

