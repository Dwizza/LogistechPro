package com.logistechpro.controller;

import com.logistechpro.dto.Request.ClientRegisterRequest;
import com.logistechpro.dto.Request.LoginRequest;
import com.logistechpro.dto.Response.AuthResponse;
import com.logistechpro.dto.Response.ClientResponse;
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
}
