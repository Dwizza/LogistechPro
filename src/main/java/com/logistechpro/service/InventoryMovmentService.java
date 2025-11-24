package com.logistechpro.service;

import com.logistechpro.dto.Request.InventoryMovmentRequest;
import com.logistechpro.dto.Response.InventoryMovmentResponse;

import java.util.List;

public interface InventoryMovmentService {
    List<InventoryMovmentResponse> getAll();
    InventoryMovmentResponse create(InventoryMovmentRequest request);
    InventoryMovmentResponse inbound(InventoryMovmentRequest request);
    InventoryMovmentResponse outbound(InventoryMovmentRequest request);
    InventoryMovmentResponse adjust(InventoryMovmentRequest request);
}

