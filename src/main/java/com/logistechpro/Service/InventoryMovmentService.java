package com.logistechpro.Service;

import com.logistechpro.DTO.Request.InventoryMovmentRequest;
import com.logistechpro.DTO.Response.InventoryMovmentResponse;

import java.util.List;

public interface InventoryMovmentService {
    List<InventoryMovmentResponse> getAll();
    InventoryMovmentResponse create(InventoryMovmentRequest request);
    InventoryMovmentResponse inbound(InventoryMovmentRequest request);
    InventoryMovmentResponse outbound(InventoryMovmentRequest request);
    InventoryMovmentResponse adjust(InventoryMovmentRequest request);
}

