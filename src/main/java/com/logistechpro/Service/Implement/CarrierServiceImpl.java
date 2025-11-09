package com.logistechpro.Service.Implement;

import com.logistechpro.DTO.Request.CarrierRequest;
import com.logistechpro.DTO.Response.CarrierResponse;
import com.logistechpro.Mapper.CarrierMapper;
import com.logistechpro.Models.Carrier;
import com.logistechpro.Repository.CarrierRepository;
import lombok.RequiredArgsConstructor;
import  com.logistechpro.Service.CarrierService;
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
