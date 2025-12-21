package com.logistechpro.service;

import com.logistechpro.dto.Request.LoginRequest;
import com.logistechpro.dto.Request.ClientRegisterRequest;
import com.logistechpro.dto.Response.AuthResponse;
import com.logistechpro.dto.Response.ClientResponse;

public interface AuthService {
    ClientResponse register(ClientRegisterRequest request);
    AuthResponse login(LoginRequest request);
}
