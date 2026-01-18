package com.logistechpro.service;

import com.logistechpro.dto.request.ProductRequest;
import com.logistechpro.dto.response.ProductResponse;
import com.logistechpro.dto.response.ProductWithInventoryResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAll();
    ProductResponse getById(Long id);
    ProductResponse getBySku(String sku);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);

    // NEW: product + warehouses + quantities
    List<ProductWithInventoryResponse> getAllWithInventory();
    ProductWithInventoryResponse getByIdWithInventory(Long id);
}
