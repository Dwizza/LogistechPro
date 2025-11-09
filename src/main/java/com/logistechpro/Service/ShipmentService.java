package com.logistechpro.Service;

import com.logistechpro.DTO.Request.ShipmentRequest;
import com.logistechpro.DTO.Response.ShipmentResponse;

public interface ShipmentService {
    ShipmentResponse createShipment(ShipmentRequest request);
}
