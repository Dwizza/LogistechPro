package com.logistechpro.Mapper;

import com.logistechpro.Models.Product;
import com.logistechpro.DTO.Request.ProductRequest;
import com.logistechpro.DTO.Response.ProductResponse;
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