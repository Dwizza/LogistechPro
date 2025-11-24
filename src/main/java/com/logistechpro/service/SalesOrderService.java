package com.logistechpro.service;

import com.logistechpro.dto.Response.SalesOrderResponse;
import com.logistechpro.dto.SalesOrderRequest;

import java.util.List;

public interface SalesOrderService {
    SalesOrderResponse create(SalesOrderRequest request);
    SalesOrderResponse findById(Long id);
    List<SalesOrderResponse> findAll();
    List<SalesOrderResponse> findByStatus(String status);
}
