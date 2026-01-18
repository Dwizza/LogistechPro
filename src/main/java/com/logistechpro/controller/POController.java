package com.logistechpro.controller;

import com.logistechpro.dto.request.PORequest;
import com.logistechpro.dto.response.POResponse;
import com.logistechpro.service.POService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/purchase-orders")
public class POController {

    private final POService poService;

    @GetMapping
    public ResponseEntity<List<POResponse>> getAll() {
        return ResponseEntity.ok(poService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<POResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(poService.getById(id));
    }

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
