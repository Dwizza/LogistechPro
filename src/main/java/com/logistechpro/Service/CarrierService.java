package com.logistechpro.Service;

import com.logistechpro.DTO.Request.CarrierRequest;
import com.logistechpro.DTO.Response.CarrierResponse;

import java.util.List;

public interface CarrierService {
    CarrierResponse createCarrier(CarrierRequest request);
    List<CarrierResponse> getAllCarriers();
}
