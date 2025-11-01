package com.logistechpro.Service;

import com.logistechpro.DTO.Request.ClientLoginRequest;
import com.logistechpro.DTO.Request.ClientRegisterRequest;
import com.logistechpro.DTO.Response.ClientResponse;

public interface ClientAuthService {
    ClientResponse register(ClientRegisterRequest request);
    ClientResponse login(ClientLoginRequest request);
}

