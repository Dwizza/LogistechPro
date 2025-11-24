package com.logistechpro.service.Implement;

import com.logistechpro.dto.Request.CarrierRequest;
import com.logistechpro.dto.Response.CarrierResponse;
import com.logistechpro.mapper.CarrierMapper;
import com.logistechpro.models.Carrier;
import com.logistechpro.repository.CarrierRepository;
import lombok.RequiredArgsConstructor;
import  com.logistechpro.service.CarrierService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepository carrierRepo;
    private final CarrierMapper carrierMapper;

    public CarrierResponse createCarrier(CarrierRequest request) {
        if (carrierRepo.findByContactEmail(request.getContactEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        Carrier carrier = carrierMapper.toEntity(request);
        Carrier savedCarrier = carrierRepo.save(carrier);

        return carrierMapper.toResponse(savedCarrier);
    }

    @Override
    public List<CarrierResponse> getAllCarriers() {
        List<Carrier> carriers = carrierRepo.findAll();
        return carriers.stream()
                .map(carrierMapper::toResponse)
                .toList();
    }
}
