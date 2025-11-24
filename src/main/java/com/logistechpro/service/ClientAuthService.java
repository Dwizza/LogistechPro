package com.logistechpro.service;

import com.logistechpro.dto.Request.ClientLoginRequest;
import com.logistechpro.dto.Request.ClientRegisterRequest;
import com.logistechpro.dto.Response.ClientResponse;

public interface ClientAuthService {
    ClientResponse register(ClientRegisterRequest request);
    ClientResponse login(ClientLoginRequest request);
}

