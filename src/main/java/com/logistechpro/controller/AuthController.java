package com.logistechpro.controller;

import com.logistechpro.dto.request.ClientRegisterRequest;
import com.logistechpro.dto.request.LoginRequest;
import com.logistechpro.dto.request.TokenRefreshRequest;
import com.logistechpro.dto.response.AuthResponse;
import com.logistechpro.dto.response.ClientResponse;
import com.logistechpro.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<ClientResponse> registerClient(@Valid @RequestBody ClientRegisterRequest request) {

        ClientResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return authService.refreshToken(request.getRefreshToken());
    }


}
