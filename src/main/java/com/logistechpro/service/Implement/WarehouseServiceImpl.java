package com.logistechpro.service.Implement;

import com.logistechpro.dto.Request.WarehouseRequest;
import com.logistechpro.dto.Response.WarehouseResponse;
import com.logistechpro.mapper.WarehouseMapper;
import com.logistechpro.models.Warehouse;
import com.logistechpro.repository.WarehouseRepository;
import com.logistechpro.service.WarehouseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;


    @Override
    public List<WarehouseResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseResponse getById(Long id) {
        Warehouse warehouse = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return mapper.toResponse(warehouse);
    }

    @Override
    public WarehouseResponse create(WarehouseRequest request) {
        if (repository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Warehouse code already exists");
        }
        Warehouse warehouse = mapper.toEntity(request);
        return mapper.toResponse(repository.save(warehouse));
    }

    @Override
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        existing.setCode(request.getCode());
        existing.setName(request.getName());
        existing.setActive(request.isActive());
        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
