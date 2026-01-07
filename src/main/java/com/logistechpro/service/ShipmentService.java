package com.logistechpro.service;

import com.logistechpro.dto.request.ShipmentRequest;
import com.logistechpro.dto.response.ShipmentResponse;

public interface ShipmentService {
    ShipmentResponse createShipment(ShipmentRequest request);
}
