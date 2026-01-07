package com.logistechpro.service;

import com.logistechpro.dto.request.CarrierRequest;
import com.logistechpro.dto.response.CarrierResponse;

import java.util.List;

public interface CarrierService {
    CarrierResponse createCarrier(CarrierRequest request);
    List<CarrierResponse> getAllCarriers();
}
