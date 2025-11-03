package com.logistechpro.controller;

import com.logistechpro.DTO.Request.InventoryMovmentRequest;
import com.logistechpro.DTO.Response.InventoryMovmentResponse;
import com.logistechpro.Models.Enums.MovementType;
import com.logistechpro.Service.InventoryMovmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/movements")
public class InventoryMovmentController {

    private final InventoryMovmentService service;


    @GetMapping
    public ResponseEntity<List<InventoryMovmentResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<InventoryMovmentResponse> create(@Valid @RequestBody InventoryMovmentRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PostMapping("/inbound")
    public ResponseEntity<InventoryMovmentResponse> inbound(@Valid @RequestBody InventoryMovmentRequest req) {
        req.setType(MovementType.INBOUND);
        return ResponseEntity.ok(service.inbound(req));
    }

    @PostMapping("/outbound")
    public ResponseEntity<InventoryMovmentResponse> outbound(@Valid @RequestBody InventoryMovmentRequest req) {
        req.setType(MovementType.OUTBOUND);
        return ResponseEntity.ok(service.outbound(req));
    }

    @PostMapping("/adjustment")
    public ResponseEntity<InventoryMovmentResponse> adjustment(@Valid @RequestBody InventoryMovmentRequest req) {
        req.setType(MovementType.ADJUSTMENT);
        return ResponseEntity.ok(service.adjust(req));
    }
}

