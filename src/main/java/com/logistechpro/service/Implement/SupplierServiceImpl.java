package com.logistechpro.service.Implement;

import com.logistechpro.dto.request.SupplierRequest;
import com.logistechpro.dto.response.SupplierResponse;
import com.logistechpro.mapper.SupplierMapper;
import com.logistechpro.models.Supplier;
import com.logistechpro.repository.SupplierRepository;
import com.logistechpro.service.SupplierService;
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
