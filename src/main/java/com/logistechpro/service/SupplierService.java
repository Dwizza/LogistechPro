package com.logistechpro.service;

import com.logistechpro.dto.request.SupplierRequest;
import com.logistechpro.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    List<SupplierResponse> getAll();
    SupplierResponse getById(Long id);
    SupplierResponse create(SupplierRequest request);
    SupplierResponse update(Long id, SupplierRequest request);
    void delete(Long id);
}
