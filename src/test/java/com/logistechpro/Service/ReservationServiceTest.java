package com.logistechpro.Service;

import com.logistechpro.DTO.Response.ReservationResponse;
import com.logistechpro.Models.*;
import com.logistechpro.Models.Enums.MovementType;
import com.logistechpro.Models.Enums.OrderStatus;
import com.logistechpro.Repository.InventoryMovmentRepository;
import com.logistechpro.Repository.InventoryRepository;
import com.logistechpro.Repository.SalesOrderRepository;
import com.logistechpro.Repository.WarehouseRepository;
import com.logistechpro.Service.Implement.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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

    // ===================== Test Data =====================
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
        // Créer le client
        client = Client.builder()
                .id(1L)
                .telephone("1234567890")
                .build();

        // Créer l'entrepôt principal
        warehouse = Warehouse.builder()
                .id(1L)
                .code("WH001")
                .name("Main Warehouse")
                .active(true)
                .build();

        // Créer les produits
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

        // Créer les lignes de commande
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

        // Créer l'inventaire
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

        // Créer la commande
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

    // ===================== TESTS - CAS DE SUCCÈS =====================

    /**
     * TEST 1 : Réserver une commande avec stock suffisant
     * - 10 unités demandées, 10 disponibles → Succès
     * - 5 unités demandées, 5 disponibles → Succès
     * Résultat attendu : Statut RESERVED, pas de manque
     */
    @Test
    void testReserveOrder_FullReservation_Success() {
        // ARRANGE
        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepo.findByProductAndWarehouse(product1, warehouse))
                .thenReturn(Optional.of(inventory1));
        when(inventoryRepo.findByProductAndWarehouse(product2, warehouse))
                .thenReturn(Optional.of(inventory2));
        when(inventoryRepo.save(any())).thenReturn(null);
        when(salesOrderRepo.save(any())).thenReturn(order);

        // ACT
        ReservationResponse response = reservationService.reserveOrder(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.RESERVED.name(), response.getStatus());
        assertTrue(response.getShortages().isEmpty());

        // Vérifier que les quantités réservées ont été mises à jour
        verify(inventoryRepo, times(2)).save(any());
        verify(salesOrderRepo).save(order);
    }

    /**
     * TEST 2 : Réserver une commande avec stock partiellement disponible
     * - 10 unités demandées, 5 disponibles → Réservé 5, Manque 5
     * - 5 unités demandées, 5 disponibles → Succès
     * Résultat attendu : Statut PARTIALLY_RESERVED, manque pour produit1
     */
    @Test
    void testReserveOrder_PartialReservation_WithShortage() {
        // ARRANGE
        line1.setQuantity(10);
        inventory1.setQtyOnHand(5); // Seulement 5 disponibles

        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepo.findByProductAndWarehouse(product1, warehouse))
                .thenReturn(Optional.of(inventory1));
        when(inventoryRepo.findByProductAndWarehouse(product2, warehouse))
                .thenReturn(Optional.of(inventory2));
        when(inventoryRepo.save(any())).thenReturn(null);
        when(warehouseRepo.findAll()).thenReturn(new ArrayList<>()); // Pas d'autres entrepôts
        when(inventoryMovmentRepo.save(any())).thenReturn(null);
        when(salesOrderRepo.save(any())).thenReturn(order);

        // ACT
        ReservationResponse response = reservationService.reserveOrder(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.PARTIALLY_RESERVED.name(), response.getStatus());
        assertFalse(response.getShortages().isEmpty());
        assertTrue(response.getShortages().containsKey("Product A"));
        assertEquals(5, response.getShortages().get("Product A"));

        verify(inventoryMovmentRepo, times(1)).save(any()); // Adjustment
    }

    // ===================== TESTS - CAS D'ERREUR =====================

    /**
     * TEST 3 : Essayer de réserver une commande qui n'existe pas
     * Résultat attendu : Exception RuntimeException
     */
    @Test
    void testReserveOrder_OrderNotFound_ThrowsException() {
        // ARRANGE
        when(salesOrderRepo.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> reservationService.reserveOrder(999L),
                "Devrait lever une exception pour une commande inexistante");
    }

    /**
     * TEST 4 : Essayer de réserver une commande qui n'est pas en statut CREATED
     * Résultat attendu : Exception RuntimeException
     */
    @Test
    void testReserveOrder_InvalidOrderStatus_ThrowsException() {
        // ARRANGE
        order.setStatus(OrderStatus.RESERVED); // Statut invalide
        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> reservationService.reserveOrder(1L),
                "Devrait lever une exception pour un statut invalide");
    }

    /**
     * TEST 5 : Essayer de réserver quand l'inventaire n'existe pas
     * Résultat attendu : Exception RuntimeException
     */
    @Test
    void testReserveOrder_NoInventory_ThrowsException() {
        // ARRANGE
        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepo.findByProductAndWarehouse(product1, warehouse))
                .thenReturn(Optional.empty()); // Pas d'inventaire

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> reservationService.reserveOrder(1L),
                "Devrait lever une exception quand l'inventaire n'existe pas");
    }

    // ===================== TESTS - RECHECK RESERVATION =====================

    /**
     * TEST 6 : Réappliquer la réservation après que du stock soit devenu disponible
     * - Commande en PARTIALLY_RESERVED avec 5 unités en manque
     * - Après recheck : 5 unités deviennent disponibles
     * Résultat attendu : Statut devient RESERVED
     */
    @Test
    void testRecheckReservation_NoMoreShortage_Success() {
        // ARRANGE
        order.setStatus(OrderStatus.PARTIALLY_RESERVED);
        line1.setQtyReserved(5);
        line1.setQtyShortage(5);
        line1.setBackOrder(true);

        inventory1.setQtyOnHand(10); // Du stock est devenu disponible
        inventory1.setQtyReserved(5);

        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepo.findByProductAndWarehouse(product1, warehouse))
                .thenReturn(Optional.of(inventory1));
        when(inventoryRepo.save(any())).thenReturn(null);
        when(salesOrderRepo.save(any())).thenReturn(order);

        // ACT
        ReservationResponse response = reservationService.recheckReservation(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.RESERVED.name(), response.getStatus());
        assertTrue(response.getShortages().isEmpty());

        verify(inventoryRepo, times(1)).save(inventory1);
        verify(salesOrderRepo).save(order);
    }

    /**
     * TEST 7 : Réappliquer la réservation quand le stock reste insuffisant
     * - Commande en PARTIALLY_RESERVED avec 5 unités en manque
     * - Après recheck : seulement 2 unités disponibles
     * Résultat attendu : Statut reste PARTIALLY_RESERVED, manque de 3 unités
     */
    @Test
    void testRecheckReservation_StillShortage_PartialUpdate() {
        // ARRANGE
        order.setStatus(OrderStatus.PARTIALLY_RESERVED);
        line1.setQtyReserved(5);
        line1.setQtyShortage(5);
        line1.setBackOrder(true);

        inventory1.setQtyOnHand(7); // Seulement 7 disponibles, 2 de plus
        inventory1.setQtyReserved(5);

        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepo.findByProductAndWarehouse(product1, warehouse))
                .thenReturn(Optional.of(inventory1));
        when(inventoryRepo.save(any())).thenReturn(null);
        when(salesOrderRepo.save(any())).thenReturn(order);

        // ACT
        ReservationResponse response = reservationService.recheckReservation(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.PARTIALLY_RESERVED.name(), response.getStatus());
        assertFalse(response.getShortages().isEmpty());
        assertEquals(3, response.getShortages().get("Product A"));

        verify(inventoryRepo, times(1)).save(inventory1);
    }

    /**
     * TEST 8 : Essayer de recheck une commande qui n'est pas PARTIALLY_RESERVED
     * Résultat attendu : Exception RuntimeException
     */
    @Test
    void testRecheckReservation_InvalidStatus_ThrowsException() {
        // ARRANGE
        order.setStatus(OrderStatus.RESERVED); // Pas PARTIALLY_RESERVED
        when(salesOrderRepo.findById(1L)).thenReturn(Optional.of(order));

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> reservationService.recheckReservation(1L),
                "Devrait lever une exception si le statut n'est pas PARTIALLY_RESERVED");
    }

    /**
     * TEST 9 : Recheck sur une commande inexistante
     * Résultat attendu : Exception RuntimeException
     */
    @Test
    void testRecheckReservation_OrderNotFound_ThrowsException() {
        // ARRANGE
        when(salesOrderRepo.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> reservationService.recheckReservation(999L),
                "Devrait lever une exception pour une commande inexistante");
    }
}
