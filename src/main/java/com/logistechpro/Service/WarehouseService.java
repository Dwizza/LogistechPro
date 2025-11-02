package com.logistechpro.Service;

import com.logistechpro.DTO.Request.WarehouseRequest;
import com.logistechpro.DTO.Response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {
    List<WarehouseResponse> getAll();
    WarehouseResponse getById(Long id);
    WarehouseResponse create(WarehouseRequest request);
    WarehouseResponse update(Long id, WarehouseRequest request);
    void delete(Long id);
}
