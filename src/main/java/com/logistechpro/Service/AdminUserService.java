package com.logistechpro.Service;

import com.logistechpro.DTO.Request.WarehouseManagerCreateRequest;
import com.logistechpro.DTO.Response.UserResponse;

public interface AdminUserService {
    UserResponse createWarehouseManager(WarehouseManagerCreateRequest request);
    UserResponse getCurrentAdmin();
}
