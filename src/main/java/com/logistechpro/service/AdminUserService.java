package com.logistechpro.service;

import com.logistechpro.dto.Request.WarehouseManagerCreateRequest;
import com.logistechpro.dto.Response.UserResponse;

public interface AdminUserService {
    UserResponse createWarehouseManager(WarehouseManagerCreateRequest request);
    UserResponse getCurrentAdmin();
}
