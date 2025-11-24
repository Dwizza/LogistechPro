package com.logistechpro.service;

import com.logistechpro.dto.Request.CarrierRequest;
import com.logistechpro.dto.Response.CarrierResponse;

import java.util.List;

public interface CarrierService {
    CarrierResponse createCarrier(CarrierRequest request);
    List<CarrierResponse> getAllCarriers();
}
