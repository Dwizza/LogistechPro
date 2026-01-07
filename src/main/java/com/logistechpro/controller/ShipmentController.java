package com.logistechpro.controller;

import com.logistechpro.dto.request.ShipmentRequest;
import com.logistechpro.dto.response.ShipmentResponse;
import com.logistechpro.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.ok(shipmentService.createShipment(request));
    }
}
