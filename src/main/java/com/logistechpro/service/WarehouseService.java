package com.logistechpro.service;

import com.logistechpro.dto.request.WarehouseRequest;
import com.logistechpro.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {
    List<WarehouseResponse> getAll();
    WarehouseResponse getById(Long id);
    WarehouseResponse create(WarehouseRequest request);
    WarehouseResponse update(Long id, WarehouseRequest request);
    void delete(Long id);
}
