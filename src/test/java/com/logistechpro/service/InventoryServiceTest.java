package com.logistechpro.service;

import com.logistechpro.dto.request.InventoryRequest;
import com.logistechpro.dto.response.InventoryResponse;
import com.logistechpro.mapper.InventoryMapper;
import com.logistechpro.models.Inventory;
import com.logistechpro.models.Product;
import com.logistechpro.models.Warehouse;
import com.logistechpro.repository.InventoryRepository;
import com.logistechpro.repository.ProductRepository;
import com.logistechpro.repository.WarehouseRepository;
import com.logistechpro.service.Implement.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    InventoryRepository inventoryRepo;

    @Mock
    ProductRepository productRepo;

    @Mock
    WarehouseRepository warehouseRepo;

    @Mock
    InventoryMapper mapper;

    @InjectMocks
    InventoryServiceImpl inventoryService;

    private Long INVENTORY_ID;
    private Long PRODUCT_ID;
    private Long WAREHOUSE_ID;
    private int QTY_ON_HAND;
    private int QTY_RESERVED;

    private Product mockProduct;
    private Warehouse mockWarehouse;
    private Inventory mockInventory;
    private InventoryResponse mockResponse;
    private InventoryRequest mockRequest;

    @BeforeEach
    void setUp() {
        INVENTORY_ID = 1L;
        PRODUCT_ID = 10L;
        WAREHOUSE_ID = 20L;
        QTY_ON_HAND = 50;
        QTY_RESERVED = 10;

        mockProduct = Product.builder()
                .id(PRODUCT_ID)
                .name("TestProduct")
                .build();

        mockWarehouse = Warehouse.builder()
                .id(WAREHOUSE_ID)
                .name("TestWarehouse")
                .build();

        mockInventory = Inventory.builder()
                .id(INVENTORY_ID)
                .product(mockProduct)
                .warehouse(mockWarehouse)
                .qtyOnHand(QTY_ON_HAND)
                .qtyReserved(QTY_RESERVED)
                .build();

        mockResponse = new InventoryResponse();
        mockResponse.setId(INVENTORY_ID);
        mockResponse.setProductName("TestProduct");
        mockResponse.setWarehouseName("TestWarehouse");
        mockResponse.setQtyOnHand(QTY_ON_HAND);
        mockResponse.setQtyReserved(QTY_RESERVED);
        mockResponse.setAvailable(QTY_ON_HAND - QTY_RESERVED);

        mockRequest = new InventoryRequest();
        mockRequest.setProductId(PRODUCT_ID);
        mockRequest.setWarehouseId(WAREHOUSE_ID);
        mockRequest.setQtyOnHand(QTY_ON_HAND);
        mockRequest.setQtyReserved(QTY_RESERVED);
    }

    @Test
    void getAll_shouldReturnListOfResponses() {
        List<Inventory> inventoryList = Arrays.asList(mockInventory);

        when(inventoryRepo.findAll()).thenReturn(inventoryList);
        when(mapper.toResponse(mockInventory)).thenReturn(mockResponse);

        List<InventoryResponse> result = inventoryService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(inventoryRepo, times(1)).findAll();
        verify(mapper, times(1)).toResponse(mockInventory);
    }

    @Test
    void getAll_whenEmpty_shouldReturnEmptyList() {
        when(inventoryRepo.findAll()).thenReturn(Arrays.asList());

        List<InventoryResponse> result = inventoryService.getAll();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(inventoryRepo, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnResponse_whenFound() {
        when(inventoryRepo.findById(INVENTORY_ID)).thenReturn(Optional.of(mockInventory));
        when(mapper.toResponse(mockInventory)).thenReturn(mockResponse);

        InventoryResponse result = inventoryService.getById(INVENTORY_ID);

        assertNotNull(result);
        assertEquals(INVENTORY_ID, result.getId());
        verify(inventoryRepo, times(1)).findById(INVENTORY_ID);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(inventoryRepo.findById(INVENTORY_ID)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            inventoryService.getById(INVENTORY_ID);
        });
        assertEquals("Inventory not found", thrown.getMessage());
        verify(inventoryRepo, times(1)).findById(INVENTORY_ID);
    }

    @Test
    void create_shouldSaveInventoryAndReturnResponse() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));
        when(warehouseRepo.findById(WAREHOUSE_ID)).thenReturn(Optional.of(mockWarehouse));
        when(inventoryRepo.save(any(Inventory.class))).thenReturn(mockInventory);
        when(mapper.toResponse(mockInventory)).thenReturn(mockResponse);

        InventoryResponse result = inventoryService.create(mockRequest);

        assertNotNull(result);
        assertEquals(INVENTORY_ID, result.getId());
        verify(inventoryRepo, times(1)).save(any(Inventory.class));
        verify(mapper, times(1)).toResponse(mockInventory);
    }

    @Test
    void create_shouldThrowException_whenProductNotFound() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            inventoryService.create(mockRequest);
        });
        assertEquals("Product not found", thrown.getMessage());
        verify(inventoryRepo, never()).save(any(Inventory.class));
    }

    @Test
    void create_shouldThrowException_whenWarehouseNotFound() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));
        when(warehouseRepo.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            inventoryService.create(mockRequest);
        });
        assertEquals("Warehouse not found", thrown.getMessage());
        verify(inventoryRepo, never()).save(any(Inventory.class));
    }

    @Test
    void update_shouldUpdateInventoryAndReturnResponse() {
        int newQtyOnHand = 100;
        int newQtyReserved = 20;

        InventoryRequest updateRequest = new InventoryRequest();
        updateRequest.setQtyOnHand(newQtyOnHand);
        updateRequest.setQtyReserved(newQtyReserved);

        Inventory existingInventory = Inventory.builder()
                .id(INVENTORY_ID)
                .qtyOnHand(QTY_ON_HAND)
                .qtyReserved(QTY_RESERVED)
                .build();

        InventoryResponse updatedResponse = new InventoryResponse();
        updatedResponse.setId(INVENTORY_ID);
        updatedResponse.setQtyOnHand(newQtyOnHand);
        updatedResponse.setQtyReserved(newQtyReserved);
        updatedResponse.setAvailable(newQtyOnHand - newQtyReserved);

        when(inventoryRepo.findById(INVENTORY_ID)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepo.save(existingInventory)).thenReturn(existingInventory);
        when(mapper.toResponse(existingInventory)).thenReturn(updatedResponse);

        InventoryResponse result = inventoryService.update(INVENTORY_ID, updateRequest);

        assertNotNull(result);
        assertEquals(newQtyOnHand, result.getQtyOnHand());
        assertEquals(newQtyReserved, result.getQtyReserved());
        verify(inventoryRepo, times(1)).findById(INVENTORY_ID);
        verify(inventoryRepo, times(1)).save(existingInventory);
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        InventoryRequest updateRequest = new InventoryRequest();
        updateRequest.setQtyOnHand(100);

        when(inventoryRepo.findById(INVENTORY_ID)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            inventoryService.update(INVENTORY_ID, updateRequest);
        });

        assertEquals("Inventory not found", thrown.getMessage());
        verify(inventoryRepo, times(1)).findById(INVENTORY_ID);
        verify(inventoryRepo, never()).save(any(Inventory.class));
    }

    @Test
    void delete_shouldCallDeleteById() {
        doNothing().when(inventoryRepo).deleteById(INVENTORY_ID);

        inventoryService.delete(INVENTORY_ID);

        verify(inventoryRepo, times(1)).deleteById(INVENTORY_ID);
    }

    @Test
    void delete_shouldCallDeleteByIdMultipleTimes() {
        doNothing().when(inventoryRepo).deleteById(anyLong());

        inventoryService.delete(1L);
        inventoryService.delete(2L);
        inventoryService.delete(3L);

        verify(inventoryRepo, times(3)).deleteById(anyLong());
    }
}