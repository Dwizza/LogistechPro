package com.logistechpro.mapper;

import com.logistechpro.dto.request.SupplierRequest;
import com.logistechpro.dto.response.SupplierResponse;
import com.logistechpro.models.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toEntity(SupplierRequest request);

    SupplierResponse toResponse(Supplier supplier);

    void updateEntityFromDto(SupplierRequest request, @MappingTarget Supplier supplier);
}

