package com.logistechpro.Service;

import com.logistechpro.DTO.Request.ProductRequest;
import com.logistechpro.DTO.Response.ProductResponse;
import com.logistechpro.Mapper.ProductMapper;
import com.logistechpro.Models.Product;
import com.logistechpro.Repository.InventoryRepository;
import com.logistechpro.Repository.ProductRepository;
import com.logistechpro.Repository.WarehouseRepository;
import com.logistechpro.Service.Implement.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    InventoryRepository inventoryRepository;

    @Mock
    WarehouseRepository warehouseRepository;

    @Mock
    ProductMapper mapper;

    @InjectMocks
    ProductServiceImpl productService;

    private Product testProduct;
    private ProductRequest testRequest;
    private ProductResponse testResponse;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .sku("SKU1")
                .name("Test Product")
                .category("Electronics")
                .avgPrice(new BigDecimal("99.99"))
                .active(true)
                .build();

        testRequest = new ProductRequest();
        testRequest.setSku("NEW-SKU");
        testRequest.setName("Test Product");
        testRequest.setCategory("Electronics");
        testRequest.setAvgPrice(new BigDecimal("99.99"));
        testRequest.setWarehouseId(1L);
        testRequest.setActive(true);

        testResponse = new ProductResponse();
        testResponse.setId(1L);
        testResponse.setSku("SKU1");
        testResponse.setName("Test Product");
        testResponse.setCategory("Electronics");
        testResponse.setAvgPrice(new BigDecimal("99.99"));
        testResponse.setActive(true);
    }

    @Test
    void getAll_shouldReturnMappedResponses() {
        Product p1 = Product.builder().id(1L).sku("SKU1").name("A").category("C1").avgPrice(new BigDecimal("10.50")).active(true).build();
        Product p2 = Product.builder().id(2L).sku("SKU2").name("B").category("C2").avgPrice(new BigDecimal("20.00")).active(false).build();
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        ProductResponse r1 = new ProductResponse();
        r1.setId(1L); r1.setSku("SKU1"); r1.setName("A"); r1.setCategory("C1"); r1.setAvgPrice(new BigDecimal("10.50")); r1.setActive(true);

        ProductResponse r2 = new ProductResponse();
        r2.setId(2L); r2.setSku("SKU2"); r2.setName("B"); r2.setCategory("C2"); r2.setAvgPrice(new BigDecimal("20.00")); r2.setActive(false);

        when(mapper.toResponse(p1)).thenReturn(r1);
        when(mapper.toResponse(p2)).thenReturn(r2);

        List<ProductResponse> res = productService.getAll();

        assertEquals(2, res.size());
        assertEquals("SKU1", res.get(0).getSku());
        verify(productRepository).findAll();
        verify(mapper, times(2)).toResponse(any(Product.class));
    }

    @Test
    void getAll_whenEmpty_shouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductResponse> res = productService.getAll();

        assertTrue(res.isEmpty());
        assertEquals(0, res.size());
        verify(productRepository).findAll();
    }

    @Test
    void getAll_shouldMapAllProductsCorrectly() {
        Product p1 = Product.builder().id(1L).sku("SKU1").name("Product1").category("Cat1").avgPrice(new BigDecimal("10.00")).active(true).build();
        Product p2 = Product.builder().id(2L).sku("SKU2").name("Product2").category("Cat2").avgPrice(new BigDecimal("20.00")).active(true).build();
        Product p3 = Product.builder().id(3L).sku("SKU3").name("Product3").category("Cat3").avgPrice(new BigDecimal("30.00")).active(false).build();
        when(productRepository.findAll()).thenReturn(List.of(p1, p2, p3));

        ProductResponse r1 = createProductResponse(1L, "SKU1", "Product1", "Cat1", new BigDecimal("10.00"), true);
        ProductResponse r2 = createProductResponse(2L, "SKU2", "Product2", "Cat2", new BigDecimal("20.00"), true);
        ProductResponse r3 = createProductResponse(3L, "SKU3", "Product3", "Cat3", new BigDecimal("30.00"), false);

        when(mapper.toResponse(p1)).thenReturn(r1);
        when(mapper.toResponse(p2)).thenReturn(r2);
        when(mapper.toResponse(p3)).thenReturn(r3);

        List<ProductResponse> res = productService.getAll();

        assertEquals(3, res.size());
        verify(mapper, times(3)).toResponse(any(Product.class));
    }

    @Test
    void getById_shouldReturnResponse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(mapper.toResponse(testProduct)).thenReturn(testResponse);

        ProductResponse res = productService.getById(1L);

        assertEquals(1L, res.getId());
        assertEquals("Test Product", res.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void getById_whenNotFound_shouldThrow() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.getById(99L));
        assertTrue(ex.getMessage().contains("Product not found"));
        verify(productRepository).findById(99L);
    }

    @Test
    void getById_withDifferentIds_shouldReturnCorrectProduct() {
        Product p2 = Product.builder().id(2L).sku("SKU2").name("Product2").category("Cat2").avgPrice(new BigDecimal("50.00")).active(true).build();
        ProductResponse r2 = createProductResponse(2L, "SKU2", "Product2", "Cat2", new BigDecimal("50.00"), true);

        when(productRepository.findById(2L)).thenReturn(Optional.of(p2));
        when(mapper.toResponse(p2)).thenReturn(r2);

        ProductResponse res = productService.getById(2L);

        assertEquals(2L, res.getId());
        assertEquals("Product2", res.getName());
    }

    @Test
    void getBySku_shouldReturnResponse() {
        when(productRepository.findBySku("ABCD")).thenReturn(Optional.of(testProduct));
        when(mapper.toResponse(testProduct)).thenReturn(testResponse);

        ProductResponse res = productService.getBySku("ABCD");

        assertEquals("SKU1", res.getSku());
        assertEquals(1L, res.getId());
        verify(productRepository).findBySku("ABCD");
    }

    @Test
    void getBySku_whenNotFound_shouldThrow() {
        when(productRepository.findBySku("NONEXISTENT")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.getBySku("NONEXISTENT"));
        assertTrue(ex.getMessage().contains("Product not found"));
        verify(productRepository).findBySku("NONEXISTENT");
    }

    @Test
    void getBySku_withDifferentSkus_shouldReturnCorrectProduct() {
        Product pSku2 = Product.builder().id(3L).sku("SKU-UNIQUE").name("UniqueProduct").category("Cat").avgPrice(new BigDecimal("75.00")).active(true).build();
        ProductResponse rSku2 = createProductResponse(3L, "SKU-UNIQUE", "UniqueProduct", "Cat", new BigDecimal("75.00"), true);

        when(productRepository.findBySku("SKU-UNIQUE")).thenReturn(Optional.of(pSku2));
        when(mapper.toResponse(pSku2)).thenReturn(rSku2);

        ProductResponse res = productService.getBySku("SKU-UNIQUE");

        assertEquals("SKU-UNIQUE", res.getSku());
        assertEquals("UniqueProduct", res.getName());
    }

    @Test
    void create_whenSkuUnique_shouldSaveAndReturnResponse() {
        when(productRepository.findBySku("NEW-SKU")).thenReturn(Optional.empty());
        when(mapper.toEntity(testRequest)).thenReturn(testProduct);
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(mapper.toResponse(testProduct)).thenReturn(testResponse);

        ProductResponse res = productService.create(testRequest);

        assertEquals(1L, res.getId());
        assertEquals("Test Product", res.getName());
        verify(productRepository).findBySku("NEW-SKU");
        verify(mapper).toEntity(testRequest);
        verify(productRepository).save(testProduct);
        verify(mapper).toResponse(testProduct);
    }

    @Test
    void create_whenSkuExists_shouldThrow() {
        when(productRepository.findBySku("NEW-SKU")).thenReturn(Optional.of(testProduct));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.create(testRequest));
        assertTrue(ex.getMessage().contains("SKU already exists"));
        verify(productRepository).findBySku("NEW-SKU");
        verify(productRepository, never()).save(any());
    }

    @Test
    void create_shouldMapRequestToEntity() {
        ProductRequest req = new ProductRequest();
        req.setSku("NEW-SKU");
        req.setName("New Product");
        req.setCategory("NewCat");
        req.setAvgPrice(new BigDecimal("45.50"));
        req.setWarehouseId(2L);
        req.setActive(true);

        Product newProduct = Product.builder().id(5L).sku("NEW-SKU").name("New Product").category("NewCat").avgPrice(new BigDecimal("45.50")).active(true).build();
        ProductResponse newResponse = createProductResponse(5L, "NEW-SKU", "New Product", "NewCat", new BigDecimal("45.50"), true);

        when(productRepository.findBySku("NEW-SKU")).thenReturn(Optional.empty());
        when(mapper.toEntity(req)).thenReturn(newProduct);
        when(productRepository.save(newProduct)).thenReturn(newProduct);
        when(mapper.toResponse(newProduct)).thenReturn(newResponse);

        ProductResponse res = productService.create(req);

        assertEquals("New Product", res.getName());
        assertEquals(new BigDecimal("45.50"), res.getAvgPrice());
        verify(mapper).toEntity(req);
    }

    @Test
    void update_shouldMapAndSave() {
        ProductRequest updateReq = new ProductRequest();
        updateReq.setSku("UPDATED-SKU");
        updateReq.setName("Updated Product");
        updateReq.setCategory("UpdatedCat");
        updateReq.setAvgPrice(new BigDecimal("120.00"));
        updateReq.setWarehouseId(3L);
        updateReq.setActive(true);

        Product existingProduct = Product.builder().id(1L).sku("OLD-SKU").name("Old Product").category("OldCat").avgPrice(new BigDecimal("50.00")).active(false).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        doNothing().when(mapper).updateEntityFromDto(updateReq, existingProduct);
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        when(mapper.toResponse(existingProduct)).thenReturn(testResponse);

        ProductResponse res = productService.update(1L, updateReq);

        verify(productRepository).findById(1L);
        verify(mapper).updateEntityFromDto(updateReq, existingProduct);
        verify(productRepository).save(existingProduct);
        verify(mapper).toResponse(existingProduct);
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        when(productRepository.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.update(123L, testRequest));
        assertTrue(ex.getMessage().contains("Product not found"));
        verify(productRepository).findById(123L);
        verify(mapper, never()).updateEntityFromDto(any(), any());
    }

    @Test
    void update_shouldCallMapperUpdateMethod() {
        ProductRequest req = new ProductRequest();
        req.setSku("UPD");
        req.setName("Updated");
        req.setCategory("UpdCat");
        req.setAvgPrice(new BigDecimal("85.00"));
        req.setWarehouseId(2L);
        req.setActive(true);

        Product existing = Product.builder().id(2L).sku("OLD").name("OldName").category("OldCat").avgPrice(new BigDecimal("60.00")).active(false).build();
        ProductResponse response = createProductResponse(2L, "UPD", "Updated", "UpdCat", new BigDecimal("85.00"), true);

        when(productRepository.findById(2L)).thenReturn(Optional.of(existing));
        doNothing().when(mapper).updateEntityFromDto(req, existing);
        when(productRepository.save(existing)).thenReturn(existing);
        when(mapper.toResponse(existing)).thenReturn(response);

        ProductResponse res = productService.update(2L, req);

        assertEquals(2L, res.getId());
        verify(mapper).updateEntityFromDto(req, existing);
    }

    @Test
    void delete_shouldDelegateToRepository() {
        productService.delete(7L);

        verify(productRepository).deleteById(7L);
    }

    @Test
    void delete_shouldCallDeleteForCorrectId() {
        productService.delete(99L);

        verify(productRepository).deleteById(99L);
    }

    @Test
    void delete_multipleIds_shouldDeleteEach() {
        productService.delete(1L);
        productService.delete(2L);
        productService.delete(3L);

        verify(productRepository).deleteById(1L);
        verify(productRepository).deleteById(2L);
        verify(productRepository).deleteById(3L);
        verify(productRepository, times(3)).deleteById(any());
    }

    @Test
    void getById_shouldInvokeRepositoryFindById() {
        when(productRepository.findById(5L)).thenReturn(Optional.of(testProduct));
        when(mapper.toResponse(testProduct)).thenReturn(testResponse);

        productService.getById(5L);

        verify(productRepository, times(1)).findById(5L);
    }

    @Test
    void create_shouldVerifySkuCheckBeforeSave() {
        when(productRepository.findBySku("NEW-SKU")).thenReturn(Optional.empty());
        when(mapper.toEntity(testRequest)).thenReturn(testProduct);
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(mapper.toResponse(testProduct)).thenReturn(testResponse);

        productService.create(testRequest);

        InOrder inOrder = inOrder(productRepository, mapper);
        inOrder.verify(productRepository).findBySku("NEW-SKU");
        inOrder.verify(mapper).toEntity(testRequest);
        inOrder.verify(productRepository).save(testProduct);
    }

    private ProductResponse createProductResponse(Long id, String sku, String name, String category, BigDecimal avgPrice, boolean active) {
        ProductResponse response = new ProductResponse();
        response.setId(id);
        response.setSku(sku);
        response.setName(name);
        response.setCategory(category);
        response.setAvgPrice(avgPrice);
        response.setActive(active);
        return response;
    }
}
