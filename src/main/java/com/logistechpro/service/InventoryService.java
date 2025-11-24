package com.logistechpro.service;

import com.logistechpro.dto.Request.InventoryRequest;
import com.logistechpro.dto.Response.InventoryResponse;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> getAll();
    InventoryResponse getById(Long id);
    InventoryResponse create(InventoryRequest request);
    InventoryResponse update(Long id, InventoryRequest request);
    void delete(Long id);
}
