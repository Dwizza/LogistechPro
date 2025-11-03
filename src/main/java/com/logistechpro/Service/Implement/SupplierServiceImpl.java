package com.logistechpro.Service.Implement;

import com.logistechpro.DTO.Request.SupplierRequest;
import com.logistechpro.DTO.Response.SupplierResponse;
import com.logistechpro.Mapper.SupplierMapper;
import com.logistechpro.Models.Supplier;
import com.logistechpro.Repository.SupplierRepository;
import com.logistechpro.Service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SupplierServiceImpl implements SupplierService {

        private final SupplierRepository supplierRepository;
        private final SupplierMapper mapper;

        @Override
        public List<SupplierResponse> getAll() {
            return supplierRepository.findAll()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
        }

        @Override
        public SupplierResponse getById(Long id) {
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            return mapper.toResponse(supplier);
        }

        @Override
        public SupplierResponse create(SupplierRequest request) {
            Supplier supplier = mapper.toEntity(request);
            System.out.println(supplier);
            return mapper.toResponse(supplierRepository.save(supplier));
        }

        @Override
        public SupplierResponse update(Long id, SupplierRequest request) {
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            mapper.updateEntityFromDto(request, supplier);
            return mapper.toResponse(supplierRepository.save(supplier));
        }

        @Override
        public void delete(Long id) {
            if (!supplierRepository.existsById(id)) {
                throw new RuntimeException("Supplier not found");
            }
            supplierRepository.deleteById(id);
        }
}
