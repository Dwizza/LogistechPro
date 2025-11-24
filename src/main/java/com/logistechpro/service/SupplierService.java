package com.logistechpro.service;

import com.logistechpro.dto.Request.SupplierRequest;
import com.logistechpro.dto.Response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    List<SupplierResponse> getAll();
    SupplierResponse getById(Long id);
    SupplierResponse create(SupplierRequest request);
    SupplierResponse update(Long id, SupplierRequest request);
    void delete(Long id);
}
