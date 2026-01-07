package com.logistechpro.service;

import com.logistechpro.dto.request.InventoryMovmentRequest;
import com.logistechpro.dto.response.InventoryMovmentResponse;

import java.util.List;

public interface InventoryMovmentService {
    List<InventoryMovmentResponse> getAll();
    InventoryMovmentResponse create(InventoryMovmentRequest request);
    InventoryMovmentResponse inbound(InventoryMovmentRequest request);
    InventoryMovmentResponse outbound(InventoryMovmentRequest request);
    InventoryMovmentResponse adjust(InventoryMovmentRequest request);
}

