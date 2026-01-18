package com.logistechpro.service;

import com.logistechpro.dto.request.POLineRequest;
import com.logistechpro.dto.request.PORequest;
import com.logistechpro.dto.response.POLineResponse;
import com.logistechpro.dto.response.POResponse;
import com.logistechpro.mapper.POMapper;
import com.logistechpro.models.*;
import com.logistechpro.models.Enums.PurchaseOrderStatus;
import com.logistechpro.repository.*;
import com.logistechpro.service.Implement.POServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class POServiceTest {

    @Mock
    SupplierRepository supplierRepo;

    @Mock
    WarehouseRepository warehouseRepo;

    @Mock
    ProductRepository productRepo;

    @Mock
    PORepository poRepo;

    @Mock
    POMapper poMapper;

    @Mock
    InventoryRepository inventoryRepo;

    @Mock
    InventoryMovmentRepository movmentRepo;

    @InjectMocks
    POServiceImpl poService;

    //test data
    private Long supplierId ;
    private Long warehouseId;
    private Long productId;
    private Supplier supplier;
    private Warehouse warehouse;
    private Product product;
    private PORequest poRequest;
    private POResponse poResponse;
    private PurchaseOrder purchaseOrder;
    private POLineRequest poLineRequest;
    private POLineResponse poLineResponse;
    private PurchaseOrderLine purchaseOrderLine;




    @BeforeEach
    void setUp() {
        supplierId = 1L;
        warehouseId = 1L;
        productId = 1L;
        supplier = Supplier.builder().id(supplierId).name("Test Supplier").build();
        warehouse = Warehouse.builder().id(warehouseId).name("Test Warehouse").build();
        product = Product.builder().id(productId).name("Test Product").build();
        poLineRequest = POLineRequest.builder().productId(productId).quantity(10).unitPrice(BigDecimal.valueOf(100)).build();
        poRequest = PORequest.builder()
                .supplierId(supplierId)
                .warehouseId(warehouseId)
                .lines(new java.util.ArrayList<>(java.util.List.of(poLineRequest)))
                .build();
        purchaseOrder = PurchaseOrder.builder().id(1L).supplier(supplier).warehouse(warehouse).status(PurchaseOrderStatus.CREATED).build();
        purchaseOrderLine = PurchaseOrderLine.builder().id(1L).product(product).quantity(10).unitPrice(BigDecimal.valueOf(100)).purchaseOrder(purchaseOrder).build();
        poLineResponse = POLineResponse.builder().productName(product.getName()).quantity(10).unitPrice(BigDecimal.valueOf(100)).build();
        poResponse = POResponse.builder()
                .supplierName(supplier.getName())
                .warehouseName(warehouse.getName())
                .lines(new java.util.ArrayList<>(java.util.List.of(poLineResponse)))
                .build();
    }


    @Test
    @DisplayName("Test create Purchase Order - Success")
    void testCreatePOSuccess() {
        Product newProduct = Product.builder().id(2L).name("Another Product").build();
        POLineRequest anotherLineRequest = POLineRequest.builder().productId(2L).quantity(5).unitPrice(BigDecimal.valueOf(50)).build();
        poRequest.getLines().add(anotherLineRequest);
        POLineResponse anotherLineResponse = POLineResponse.builder().productName(newProduct.getName()).quantity(5).unitPrice(BigDecimal.valueOf(50)).build();
        poResponse.getLines().add(anotherLineResponse);

        //arrange
        when(supplierRepo.findById(supplierId)).thenReturn(Optional.ofNullable(supplier));
        when(warehouseRepo.findById(warehouseId)).thenReturn(Optional.ofNullable(warehouse));
        when(productRepo.findById(productId)).thenReturn(Optional.ofNullable(product));
        when(productRepo.findById(2L)).thenReturn(Optional.of(newProduct));
        when(poRepo.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);
        when(poMapper.toResponse(any(PurchaseOrder.class))).thenReturn(poResponse);
        //act
        POResponse response = poService.create(poRequest);
        //assert
        assertNotNull(response);
      assertEquals(supplier.getName(),response.getSupplierName());
      assertEquals(2,response.getLines().size());
        //verify
        verify(poRepo,times(1)).save(any(PurchaseOrder.class));
        verify(poMapper,times(1)).toResponse(any(PurchaseOrder.class));
        verify(productRepo,times(2)).findById(any(Long.class));

    }


    @Test
    @DisplayName("Test Should Throw Exception When Creating PO with Non-Existent Supplier")
    void testCreatePOSupplierNotFound() {
        //arrange
        when(supplierRepo.findById(supplierId)).thenReturn(Optional.empty());
        //act & assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            poService.create(poRequest);
        });
        assertEquals("Supplier not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test getAll Purchase Orders - Success")
    void testGetAllPOSuccess() {
        when(poRepo.findAll()).thenReturn(List.of(purchaseOrder));
        when(poMapper.toResponse(any(PurchaseOrder.class))).thenReturn(poResponse);

        List<POResponse> res = poService.getAll();

        assertNotNull(res);
        assertEquals(1, res.size());
        verify(poRepo, times(1)).findAll();
        verify(poMapper, times(1)).toResponse(any(PurchaseOrder.class));
    }

    @Test
    @DisplayName("Test getById Purchase Order - Success")
    void testGetByIdPOSuccess() {
        when(poRepo.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(poMapper.toResponse(any(PurchaseOrder.class))).thenReturn(poResponse);

        POResponse res = poService.getById(1L);

        assertNotNull(res);
        verify(poRepo, times(1)).findById(1L);
        verify(poMapper, times(1)).toResponse(any(PurchaseOrder.class));
    }

    @Test
    @DisplayName("Test getById Purchase Order - Not Found")
    void testGetByIdPONotFound() {
        when(poRepo.findById(404L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> poService.getById(404L));
        assertTrue(ex.getMessage().contains("Purchase Order not found"));
        verify(poRepo, times(1)).findById(404L);
        verify(poMapper, never()).toResponse(any());
    }

}