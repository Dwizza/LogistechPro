package com.logistechpro.service;

import com.logistechpro.dto.request.WarehouseManagerCreateRequest;
import com.logistechpro.dto.response.UserResponse;

public interface AdminUserService {
    UserResponse createWarehouseManager(WarehouseManagerCreateRequest request);
    UserResponse getCurrentAdmin();
}
