package com.logistechpro.Service;

import com.logistechpro.DTO.Request.SupplierRequest;
import com.logistechpro.DTO.Response.SupplierResponse;
import com.logistechpro.Models.Supplier;

import java.util.List;

public interface SupplierService {
    List<SupplierResponse> getAll();
    SupplierResponse getById(Long id);
    SupplierResponse create(SupplierRequest request);
    SupplierResponse update(Long id, SupplierRequest request);
    void delete(Long id);
}
