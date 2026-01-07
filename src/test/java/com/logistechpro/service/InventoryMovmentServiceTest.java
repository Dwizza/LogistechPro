package com.logistechpro.service;

import com.logistechpro.dto.request.InventoryMovmentRequest;
import com.logistechpro.dto.response.InventoryMovmentResponse;
import com.logistechpro.mapper.InventoryMovmentMapper;
import com.logistechpro.models.Inventory;
import com.logistechpro.models.InventoryMovement;
import com.logistechpro.models.Product;
import com.logistechpro.models.Warehouse;
import com.logistechpro.models.Enums.MovementType;
import com.logistechpro.repository.InventoryMovmentRepository;
import com.logistechpro.repository.InventoryRepository;
import com.logistechpro.repository.ProductRepository;
import com.logistechpro.repository.WarehouseRepository;
import com.logistechpro.service.Implement.InventoryMovmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryMovmentServiceTest {
    @Mock InventoryMovmentRepository movementRepo;
    @Mock InventoryRepository inventoryRepo;
    @Mock ProductRepository productRepo;
    @Mock WarehouseRepository warehouseRepo;
    @Mock InventoryMovmentMapper mapper;
    @InjectMocks InventoryMovmentServiceImpl service;

    Product product;
    Warehouse warehouse;
    Inventory inventory;

    @BeforeEach
    void setup() {
        product = Product.builder().id(1L).sku("SKU").name("P").build();
        warehouse = Warehouse.builder().id(2L).code("W1").name("Main").build();
        inventory = Inventory.builder().id(3L).product(product).warehouse(warehouse).qtyOnHand(10).qtyReserved(0).build();
    }

    InventoryMovmentRequest req(MovementType type,int qty) {
        InventoryMovmentRequest r = new InventoryMovmentRequest();
        r.setProductId(1L); r.setWarehouseId(2L); r.setType(type); r.setQuantity(qty); r.setReferenceDocument("REF"); r.setDescription("D");
        return r;
    }

    @Test
    void getAll() {
        InventoryMovement m = InventoryMovement.builder().id(11L).product(product).warehouse(warehouse).qty(5).type(MovementType.INBOUND).build();
        when(movementRepo.findAll()).thenReturn(List.of(m));
        InventoryMovmentResponse resp = new InventoryMovmentResponse(); resp.setId(11L);
        when(mapper.toResponse(m)).thenReturn(resp);
        List<InventoryMovmentResponse> out = service.getAll();
        assertEquals(1, out.size());
        verify(movementRepo).findAll();
    }

    @Test
    void inbound_newInventoryCreated() {
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepo.findById(2L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepo.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.empty());
        when(inventoryRepo.save(any(Inventory.class))).thenAnswer(invocation -> {
            Inventory i = invocation.getArgument(0);
            if (i.getId() == null) i.setId(9L);
            return i;
        });
        when(movementRepo.save(any(InventoryMovement.class))).thenAnswer(invocation -> {
            InventoryMovement mv = invocation.getArgument(0);
            mv.setId(15L);
            return mv;
        });
        InventoryMovmentResponse r = new InventoryMovmentResponse(); r.setId(15L);
        when(mapper.toResponse(any())).thenReturn(r);
        InventoryMovmentResponse out = service.inbound(req(MovementType.INBOUND,5));
        assertEquals(15L,out.getId());
        verify(inventoryRepo, atLeastOnce()).save(any());
        verify(movementRepo).save(any());
    }

    @Test
    void outbound_insufficientStock() {
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepo.findById(2L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepo.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.of(inventory));
        assertThrows(RuntimeException.class, () -> service.outbound(req(MovementType.OUTBOUND,50)));
    }

    @Test
    void outbound_success() {
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepo.findById(2L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepo.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.of(inventory));
        when(inventoryRepo.save(inventory)).thenReturn(inventory);
        when(movementRepo.save(any())).thenAnswer(a -> { InventoryMovement mv = a.getArgument(0); mv.setId(21L); return mv; });
        InventoryMovmentResponse r = new InventoryMovmentResponse(); r.setId(21L);
        when(mapper.toResponse(any())).thenReturn(r);
        InventoryMovmentResponse out = service.outbound(req(MovementType.OUTBOUND,5));
        assertEquals(21L,out.getId());
        assertEquals(5, inventory.getQtyOnHand());
    }

    @Test
    void adjust_negativeResultFails() {
        inventory.setQtyOnHand(1);
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepo.findById(2L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepo.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.of(inventory));
        assertThrows(RuntimeException.class, () -> service.adjust(req(MovementType.ADJUSTMENT,-5)));
    }

    @Test
    void create_dispatchInbound() {
        InventoryMovmentServiceImpl spyService = spy(service);
        InventoryMovmentRequest r = req(MovementType.INBOUND,3);
        doReturn(new InventoryMovmentResponse()).when(spyService).inbound(r);
        InventoryMovmentResponse out = spyService.create(r);
        assertNotNull(out);
        verify(spyService).inbound(r);
    }

    @Test
    void create_dispatchOutbound() {
        InventoryMovmentServiceImpl spyService = spy(service);
        InventoryMovmentRequest r = req(MovementType.OUTBOUND,3);
        doReturn(new InventoryMovmentResponse()).when(spyService).outbound(r);
        spyService.create(r);
        verify(spyService).outbound(r);
    }

    @Test
    void create_dispatchAdjust() {
        InventoryMovmentServiceImpl spyService = spy(service);
        InventoryMovmentRequest r = req(MovementType.ADJUSTMENT,3);
        doReturn(new InventoryMovmentResponse()).when(spyService).adjust(r);
        spyService.create(r);
        verify(spyService).adjust(r);
    }
}
