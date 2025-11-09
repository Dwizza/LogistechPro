package com.logistechpro.Service;

import com.logistechpro.DTO.Response.SalesOrderResponse;
import com.logistechpro.DTO.SalesOrderRequest;

import java.util.List;

public interface SalesOrderService {
    SalesOrderResponse create(SalesOrderRequest request);
    SalesOrderResponse findById(Long id);
    List<SalesOrderResponse> findAll();
    List<SalesOrderResponse> findByStatus(String status);
}
