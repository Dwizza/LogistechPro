package com.logistechpro.Service;

import com.logistechpro.DTO.Request.InventoryRequest;
import com.logistechpro.DTO.Response.InventoryResponse;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> getAll();
    InventoryResponse getById(Long id);
    InventoryResponse create(InventoryRequest request);
    InventoryResponse update(Long id, InventoryRequest request);
    void delete(Long id);
}
