package com.logistechpro.Service.Implement;

import com.logistechpro.DTO.Request.InventoryRequest;
import com.logistechpro.DTO.Response.InventoryResponse;
import com.logistechpro.Mapper.InventoryMapper;
import com.logistechpro.Models.Inventory;
import com.logistechpro.Models.Product;
import com.logistechpro.Models.Warehouse;
import com.logistechpro.Repository.InventoryRepository;
import com.logistechpro.Repository.ProductRepository;
import com.logistechpro.Repository.WarehouseRepository;
import com.logistechpro.Service.InventoryService;
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
