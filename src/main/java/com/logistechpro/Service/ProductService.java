package com.logistechpro.Service;

import com.logistechpro.DTO.Request.ProductRequest;
import com.logistechpro.DTO.Response.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAll();
    ProductResponse getById(Long id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
}
