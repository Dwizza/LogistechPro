package com.logistechpro.service.Implement;

import com.logistechpro.dto.request.ProductRequest;
import com.logistechpro.dto.response.ProductResponse;
import com.logistechpro.dto.response.ProductWarehouseInventoryResponse;
import com.logistechpro.dto.response.ProductWithInventoryResponse;
import com.logistechpro.mapper.ProductMapper;
import com.logistechpro.models.Inventory;
import com.logistechpro.models.Product;
import com.logistechpro.repository.InventoryRepository;
import com.logistechpro.repository.ProductRepository;
import com.logistechpro.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductMapper mapper;

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapper.toResponse(product);
    }

    @Override
    public ProductResponse getBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapper.toResponse(product);
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new RuntimeException("SKU already exists");
        }
        Product product = mapper.toEntity(request);
        Product saved = productRepository.save(product);

        return mapper.toResponse(saved);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        mapper.updateEntityFromDto(request, existing);
        return mapper.toResponse(productRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductWithInventoryResponse> getAllWithInventory() {
        return productRepository.findAll()
                .stream()
                .map(this::toWithInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductWithInventoryResponse getByIdWithInventory(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toWithInventoryResponse(product);
    }

    private ProductWithInventoryResponse toWithInventoryResponse(Product product) {
        List<Inventory> inventories = inventoryRepository.findAllByProduct(product);
        if (inventories == null) {
            inventories = Collections.emptyList();
        }

        List<ProductWarehouseInventoryResponse> warehouses = inventories.stream()
                .map(inv -> ProductWarehouseInventoryResponse.builder()
                        .warehouseId(inv.getWarehouse().getId())
                        .warehouseCode(inv.getWarehouse().getCode())
                        .warehouseName(inv.getWarehouse().getName())
                        .qtyOnHand(inv.getQtyOnHand())
                        .qtyReserved(inv.getQtyReserved())
                        .qtyAvailable(inv.getAvailable())
                        // modèle actuel: prix au niveau produit
                        .price(product.getAvgPrice())
                        .build())
                .collect(Collectors.toList());

        return ProductWithInventoryResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .category(product.getCategory())
                .avgPrice(product.getAvgPrice())
                .active(product.isActive())
                .warehouses(warehouses)
                .build();
    }
}
