package com.logistechpro.service.Implement;

import com.logistechpro.dto.request.InventoryRequest;
import com.logistechpro.dto.response.InventoryResponse;
import com.logistechpro.mapper.InventoryMapper;
import com.logistechpro.models.Inventory;
import com.logistechpro.models.Product;
import com.logistechpro.models.Warehouse;
import com.logistechpro.repository.InventoryRepository;
import com.logistechpro.repository.ProductRepository;
import com.logistechpro.repository.WarehouseRepository;
import com.logistechpro.service.InventoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;
    private final WarehouseRepository warehouseRepo;
    private final InventoryMapper mapper;

    @Override
    public List<InventoryResponse> getAll() {
        return inventoryRepo.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryResponse getById(Long id) {
        Inventory inv = inventoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        return mapper.toResponse(inv);
    }

    @Override
    public InventoryResponse create(InventoryRequest request) {
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepo.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Inventory inv = Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .qtyOnHand(request.getQtyOnHand())
                .qtyReserved(request.getQtyReserved())
                .build();

        return mapper.toResponse(inventoryRepo.save(inv));
    }

    @Override
    public InventoryResponse update(Long id, InventoryRequest request) {
        Inventory inv = inventoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inv.setQtyOnHand(request.getQtyOnHand());
        inv.setQtyReserved(request.getQtyReserved());

        return mapper.toResponse(inventoryRepo.save(inv));
    }

    @Override
    public void delete(Long id) {
        inventoryRepo.deleteById(id);
    }
}
