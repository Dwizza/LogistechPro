package com.logistechpro.service;

import com.logistechpro.dto.request.WarehouseManagerCreateRequest;
import com.logistechpro.dto.response.UserResponse;

import java.util.List;

public interface AdminUserService {
    UserResponse createWarehouseManager(WarehouseManagerCreateRequest request);
    UserResponse getCurrentAdmin();

    List<UserResponse> getAllWarehouseManagers();
}
