package com.logistechpro.controller;

import com.logistechpro.dto.Request.SalesOrderRequest;
import com.logistechpro.dto.Response.SalesOrderResponse;
import com.logistechpro.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<SalesOrderResponse> create(@Valid @RequestBody SalesOrderRequest request) {
        return ResponseEntity.ok(salesOrderService.create(request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(salesOrderService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<SalesOrderResponse>> findAll() {
        return ResponseEntity.ok(salesOrderService.findAll());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SalesOrderResponse>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(salesOrderService.findByStatus(status));
    }
}
