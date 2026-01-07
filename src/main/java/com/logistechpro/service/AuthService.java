package com.logistechpro.service;

import com.logistechpro.dto.request.LoginRequest;
import com.logistechpro.dto.request.ClientRegisterRequest;
import com.logistechpro.dto.response.AuthResponse;
import com.logistechpro.dto.response.ClientResponse;

public interface AuthService {
    ClientResponse register(ClientRegisterRequest request);
    AuthResponse login(LoginRequest request);
}
