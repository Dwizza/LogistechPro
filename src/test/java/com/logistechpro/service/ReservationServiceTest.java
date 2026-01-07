package com.logistechpro.service;

import com.logistechpro.dto.response.ReservationResponse;
import com.logistechpro.models.*;
import com.logistechpro.models.Enums.OrderStatus;
import com.logistechpro.repository.InventoryMovmentRepository;
import com.logistechpro.repository.InventoryRepository;
import com.logistechpro.repository.SalesOrderRepository;
import com.logistechpro.repository.WarehouseRepository;
import com.logistechpro.service.Implement.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    SalesOrderRepository salesOrderRepo;

    @Mock
    InventoryRepository inventoryRepo;

    @Mock
    InventoryMovmentRepository inventoryMovmentRepo;

    @Mock
    WarehouseRepository warehouseRepo;

    @InjectMocks
    ReservationServiceImpl reservationService;

    private SalesOrder order;
    private Warehouse warehouse;
    private Product product1;
    private Product product2;
    private SalesOrderLine line1;
    private SalesOrderLine line2;
    private Inventory inventory1;
    private Inventory inventory2;
    private Client client;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .telephone("1234567890")
                .build();

        warehouse = Warehouse.builder()
                .id(1L)
                .code("WH001")
                .name("Main Warehouse")
                .active(true)
                .build();

        product1 = Product.builder()
                .id(1L)
                .sku("SKU001")
                .name("Product A")
                .category("Electronics")
                .avgPrice(BigDecimal.valueOf(100))
                .active(true)
                .build();

        product2 = Product.builder()
                .id(2L)
                .sku("SKU002")
                .name("Product B")
                .category("Electronics")
                .avgPrice(BigDecimal.valueOf(50))
                .active(true)
                .build();

        line1 = SalesOrderLine.builder()
                .id(1L)
                .product(product1)
                .quantity(10)
                .unitPrice(BigDecimal.valueOf(100))
                .qtyReserved(0)
                .qtyShortage(0)
                .backOrder(false)
                .build();

        line2 = SalesOrderLine.builder()
                .id(2L)
                .product(product2)
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(50))
                .qtyReserved(0)
                .qtyShortage(0)
                .backOrder(false)
                .build();

        inventory1 = Inventory.builder()
                .id(1L)
                .product(product1)
                .warehouse(warehouse)
                .qtyOnHand(10)
                .qtyReserved(0)
                .build();

        inventory2 = Inventory.builder()
                .id(2L)
                .product(product2)
                .warehouse(warehouse)
                .qtyOnHand(5)
                .qtyReserved(0)
                .build();

        order = SalesOrder.builder()
                .id(1L)
                .client(client)
                .warehouse(warehouse)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .lines(new ArrayList<>(Arrays.asList(line1, line2)))
                .build();

        line1.setSalesOrder(order);
        line2.setSalesOrder(order);
    }

    @Test
    void testReserveOrder_FullReservation_Success() {
        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepo.findByProductAndWarehouse(product1, warehouse))
                .thenReturn(Optional.of(inventory1));
        when(inventoryRepo.findByProductAndWarehouse(product2, warehouse))
                .thenReturn(Optional.of(inventory2));
        when(inventoryRepo.save(any())).thenReturn(null);
        when(salesOrderRepo.save(any())).thenReturn(order);

        ReservationResponse response = reservationService.reserveOrder(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.RESERVED.name(), response.getStatus());
        assertTrue(response.getShortages().isEmpty());

        verify(inventoryRepo, times(2)).save(any());
        verify(salesOrderRepo).save(order);
    }

    @Test
    void testReserveOrder_PartialReservation_WithShortage() {
        line1.setQuantity(10);
        inventory1.setQtyOnHand(5);

        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepo.findByProductAndWarehouse(product1, warehouse))
                .thenReturn(Optional.of(inventory1));
        when(inventoryRepo.findByProductAndWarehouse(product2, warehouse))
                .thenReturn(Optional.of(inventory2));
        when(inventoryRepo.save(any())).thenReturn(null);
        when(warehouseRepo.findAll()).thenReturn(new ArrayList<>());
        when(inventoryMovmentRepo.save(any())).thenReturn(null);
        when(salesOrderRepo.save(any())).thenReturn(order);

        ReservationResponse response = reservationService.reserveOrder(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.PARTIALLY_RESERVED.name(), response.getStatus());
        assertFalse(response.getShortages().isEmpty());
        assertTrue(response.getShortages().containsKey("Product A"));
        assertEquals(5, response.getShortages().get("Product A"));

        verify(inventoryMovmentRepo, times(1)).save(any()); // Adjustment
    }

    @Test
    void testReserveOrder_OrderNotFound_ThrowsException() {
        when(salesOrderRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservationService.reserveOrder(999L),
                "Devrait lever une exception pour une commande inexistante");
    }

    @Test
    void testReserveOrder_InvalidOrderStatus_ThrowsException() {
        order.setStatus(OrderStatus.RESERVED);
        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> reservationService.reserveOrder(1L),
                "Devrait lever une exception pour un statut invalide");
    }

    @Test
    void testReserveOrder_NoInventory_ThrowsException() {
        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepo.findByProductAndWarehouse(product1, warehouse))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservationService.reserveOrder(1L),
                "Devrait lever une exception quand l'inventaire n'existe pas");
    }

    @Test
    void testRecheckReservation_NoMoreShortage_Success() {
        order.setStatus(OrderStatus.PARTIALLY_RESERVED);
        line1.setQtyReserved(5);
        line1.setQtyShortage(5);
        line1.setBackOrder(true);

        inventory1.setQtyOnHand(10);
        inventory1.setQtyReserved(5);

        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepo.findByProductAndWarehouse(product1, warehouse))
                .thenReturn(Optional.of(inventory1));
        when(inventoryRepo.save(any())).thenReturn(null);
        when(salesOrderRepo.save(any())).thenReturn(order);

        ReservationResponse response = reservationService.recheckReservation(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.RESERVED.name(), response.getStatus());
        assertTrue(response.getShortages().isEmpty());

        verify(inventoryRepo, times(1)).save(inventory1);
        verify(salesOrderRepo).save(order);
    }

    @Test
    void testRecheckReservation_StillShortage_PartialUpdate() {
        order.setStatus(OrderStatus.PARTIALLY_RESERVED);
        line1.setQtyReserved(5);
        line1.setQtyShortage(5);
        line1.setBackOrder(true);

        inventory1.setQtyOnHand(7);
        inventory1.setQtyReserved(5);

        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepo.findByProductAndWarehouse(product1, warehouse))
                .thenReturn(Optional.of(inventory1));
        when(inventoryRepo.save(any())).thenReturn(null);
        when(salesOrderRepo.save(any())).thenReturn(order);

        ReservationResponse response = reservationService.recheckReservation(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.PARTIALLY_RESERVED.name(), response.getStatus());
        assertFalse(response.getShortages().isEmpty());
        assertEquals(3, response.getShortages().get("Product A"));

        verify(inventoryRepo, times(1)).save(inventory1);
    }

    @Test
    void testRecheckReservation_InvalidStatus_ThrowsException() {
        order.setStatus(OrderStatus.RESERVED);
        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> reservationService.recheckReservation(1L),
                "Devrait lever une exception si le statut n'est pas PARTIALLY_RESERVED");
    }

    @Test
    void testRecheckReservation_OrderNotFound_ThrowsException() {
        when(salesOrderRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservationService.recheckReservation(999L),
                "Devrait lever une exception pour une commande inexistante");
    }
}
