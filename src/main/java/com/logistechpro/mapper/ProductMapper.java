package com.logistechpro.mapper;

import com.logistechpro.models.Product;
import com.logistechpro.dto.Request.ProductRequest;
import com.logistechpro.dto.Response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);

    // pour update (map les champs du DTO vers un produit existant)
    void updateEntityFromDto(ProductRequest request, @MappingTarget Product product);
}