package com.logistechpro.controller;

import com.logistechpro.dto.request.CarrierRequest;
import com.logistechpro.dto.response.CarrierResponse;
import com.logistechpro.service.CarrierService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/carriers")
public class CarrierController {

    private final CarrierService carrierService;

    @PostMapping
    public CarrierResponse createCarrier(@Valid @RequestBody CarrierRequest request) {
        return carrierService.createCarrier(request);
    }

    @GetMapping("/all")
    public List<CarrierResponse> getCarriers() {
        return carrierService.getAllCarriers();
    }
}