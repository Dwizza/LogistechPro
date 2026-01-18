package com.logistechpro.controller;

import com.logistechpro.dto.request.ProductRequest;
import com.logistechpro.dto.response.ProductResponse;
import com.logistechpro.dto.response.ProductWithInventoryResponse;
import com.logistechpro.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    /**
     * Détails produit + warehouses + quantités (inventaire).
     */
    @GetMapping("/details")
    public ResponseEntity<List<ProductWithInventoryResponse>> getAllWithInventory() {
        return ResponseEntity.ok(productService.getAllWithInventory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    /**
     * Détails d'un produit (par id) + warehouses + quantités (inventaire).
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<ProductWithInventoryResponse> getByIdWithInventory(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getByIdWithInventory(id));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getBySku(sku));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
