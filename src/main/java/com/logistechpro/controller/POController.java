package com.logistechpro.controller;

import com.logistechpro.DTO.Request.PORequest;
import com.logistechpro.DTO.Response.POResponse;
import com.logistechpro.Service.POService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/purchase-orders")
public class POController {

    private final POService poService;

    @PostMapping
    public ResponseEntity<POResponse> create(@Valid @RequestBody PORequest request) {
        return ResponseEntity.ok(poService.create(request));
    }

    @PutMapping("/validate/{id}")
    public ResponseEntity<POResponse> validate(@PathVariable Long id) {
        return ResponseEntity.ok(poService.validatePurchaseOrder(id));
    }

}
