package com.logistechpro.Service.Implement;

import com.logistechpro.Models.Product;
import com.logistechpro.DTO.Request.ProductRequest;
import com.logistechpro.DTO.Response.ProductResponse;
import com.logistechpro.Mapper.ProductMapper;
import com.logistechpro.Repository.ProductRepository;
import com.logistechpro.Service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

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
    public ProductResponse create(ProductRequest request) {
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
}
