package com.logistechpro.service;

import com.logistechpro.dto.Request.ShipmentRequest;
import com.logistechpro.dto.Response.ShipmentResponse;

public interface ShipmentService {
    ShipmentResponse createShipment(ShipmentRequest request);
}
