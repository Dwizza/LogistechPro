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

    @PutMapping("/approve/{id}")
    public ResponseEntity<POResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(poService.approvePurchaseOrder(id));
    }

    @PutMapping("/receive/{id}")
    public ResponseEntity<POResponse> receive(@PathVariable Long id){
        return ResponseEntity.ok(poService.receivePurchaseOrder(id));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<POResponse> cancel(@PathVariable Long id){
        return ResponseEntity.ok(poService.cancelPurchaseOrder(id));
    }
}
