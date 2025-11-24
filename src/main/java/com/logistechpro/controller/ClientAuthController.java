package com.logistechpro.controller;

import com.logistechpro.dto.Request.ClientLoginRequest;
import com.logistechpro.dto.Request.ClientRegisterRequest;
import com.logistechpro.dto.Response.ClientResponse;
import com.logistechpro.service.ClientAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ClientAuthController {

    private final ClientAuthService clientAuthService;

    @PostMapping("/register")
    public ResponseEntity<ClientResponse> registerClient(@Valid @RequestBody ClientRegisterRequest request) {
        ClientResponse response = clientAuthService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<ClientResponse> loginClient(@Valid @RequestBody ClientLoginRequest request){
        ClientResponse response = clientAuthService.login(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}

