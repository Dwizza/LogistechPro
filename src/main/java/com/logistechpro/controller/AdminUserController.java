package com.logistechpro.controller;

import com.logistechpro.dto.request.WarehouseManagerCreateRequest;
import com.logistechpro.dto.response.UserResponse;
import com.logistechpro.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping("/warehouse-managers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createWarehouseManager(@Valid @RequestBody WarehouseManagerCreateRequest request) {
        UserResponse response = adminUserService.createWarehouseManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> currentAdmin() {
        return ResponseEntity.ok(adminUserService.getCurrentAdmin());
    }
}
