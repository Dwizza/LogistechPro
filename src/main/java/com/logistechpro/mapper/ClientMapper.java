package com.logistechpro.mapper;

import com.logistechpro.dto.request.ClientRegisterRequest;
import com.logistechpro.dto.response.ClientResponse;
import com.logistechpro.models.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {


    @Mapping(target = "user.name", source = "name")
    @Mapping(target = "user.email", source = "email")
    @Mapping(target = "user.passwordHash", source = "password")
    @Mapping(target = "user.role", constant = "CLIENT")
    @Mapping(target = "user.active", constant = "true")
    Client toEntity(ClientRegisterRequest request);


    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "role", source = "user.role")
    ClientResponse toResponse(Client client);
}

