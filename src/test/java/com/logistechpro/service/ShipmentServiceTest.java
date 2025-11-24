package com.logistechpro.service;

import com.logistechpro.dto.Request.ShipmentRequest;
import com.logistechpro.dto.Response.ShipmentResponse;
import com.logistechpro.mapper.ShipmentMapper;
import com.logistechpro.models.*;
import com.logistechpro.models.Enums.*;
import com.logistechpro.repository.*;
import com.logistechpro.service.Implement.ShipmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {
    @Mock
    ShipmentRepository shipmentRepository;
    @Mock
    CarrierRepository carrierRepository;
    @Mock
    SalesOrderRepository salesOrderRepository;
    @Mock
    InventoryRepository inventoryRepository;
    @Mock
    InventoryMovmentRepository inventoryMovmentRepository;
    @Mock
    ShipmentMapper shipmentMapper;
    @InjectMocks
    ShipmentServiceImpl service;

    ShipmentRequest req() {
        ShipmentRequest r = new ShipmentRequest();
        r.setSalesOrderId(1L);
        r.setCarrierId(2L);
        return r;
    }

    SalesOrder orderReserved() {
        Client client = Client.builder()
                .id(90L)
                .user(User.builder()
                        .id(77L)
                        .name("C")
                        .email("c@x.com").passwordHash("p").role(Role.CLIENT).active(true).build()).telephone("0").address("A").build();
        Warehouse wh = Warehouse.builder().id(55L).code("W1").name("Main").build();
        Product p = Product.builder().id(11L).sku("SKU").name("P").avgPrice(BigDecimal.TEN).active(true).build();
        SalesOrderLine line = SalesOrderLine.builder().id(101L).product(p).quantity(3).unitPrice(BigDecimal.TEN).qtyReserved(3).salesOrder(null).build();
        SalesOrder so = SalesOrder.builder().id(1L).client(client).warehouse(wh).status(OrderStatus.RESERVED).createdAt(java.time.LocalDateTime.now()).lines(new java.util.ArrayList<>()).build();
        line.setSalesOrder(so); so.getLines().add(line); return so;
    }

    Carrier activeCarrier() { return Carrier.builder().id(2L).name("Carrier").contactEmail("c@x.com").capacityWeight(BigDecimal.TEN).capacityVolume(BigDecimal.TEN).active(true).status(CarrierStatus.ACTIVE).build(); }

    @Test
    void createShipment_success() {
        SalesOrder so = orderReserved();
        Carrier carrier = activeCarrier();
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(so));
        when(carrierRepository.findById(2L)).thenReturn(Optional.of(carrier));
        Inventory inv = Inventory.builder().id(800L).product(so.getLines().get(0).getProduct()).warehouse(so.getWarehouse()).qtyOnHand(10).qtyReserved(3).build();
        when(inventoryRepository.findByProductAndWarehouse(so.getLines().get(0).getProduct(), so.getWarehouse())).thenReturn(Optional.of(inv));
        when(shipmentRepository.save(any())).thenAnswer(a -> { Shipment s = a.getArgument(0); s.setId(999L); return s; });
        when(inventoryRepository.save(inv)).thenReturn(inv);
        when(inventoryMovmentRepository.save(any())).thenAnswer(a -> { InventoryMovement m = a.getArgument(0); m.setId(444L); return m; });
        ShipmentResponse resp = new ShipmentResponse(); resp.setId(999L); resp.setSalesOrderId(1L); resp.setCarrierId(2L); resp.setStatus(ShipmentStatus.PLANNED);
        when(shipmentMapper.toResponse(any())).thenReturn(resp);
        ShipmentResponse out = service.createShipment(req());
        assertEquals(999L,out.getId());
        assertEquals(OrderStatus.SHIPPED, so.getStatus());
        assertEquals(7, inv.getQtyOnHand());
        assertEquals(0, inv.getQtyReserved());
        verify(shipmentRepository).save(any());
        verify(inventoryMovmentRepository).save(any());
    }

    @Test
    void createShipment_orderNotFound() {
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.createShipment(req()));
    }

    @Test
    void createShipment_orderWrongStatus() {
        SalesOrder so = orderReserved();
        so.setStatus(OrderStatus.CREATED);
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(so));
        assertThrows(RuntimeException.class, () -> service.createShipment(req()));
    }

    @Test
    void createShipment_carrierNotFound() {
        SalesOrder so = orderReserved();
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(so));
        when(carrierRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.createShipment(req()));
    }

    @Test
    void createShipment_carrierInactive() {
        SalesOrder so = orderReserved();
        Carrier carrier = activeCarrier(); carrier.setStatus(CarrierStatus.INACTIVE);
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(so));
        when(carrierRepository.findById(2L)).thenReturn(Optional.of(carrier));
        assertThrows(RuntimeException.class, () -> service.createShipment(req()));
    }

    @Test
    void createShipment_lineInventoryMissing() {
        SalesOrder so = orderReserved();
        Carrier carrier = activeCarrier();
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(so));
        when(carrierRepository.findById(2L)).thenReturn(Optional.of(carrier));
        when(inventoryRepository.findByProductAndWarehouse(any(), any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.createShipment(req()));
    }

    @Test
    void createShipment_notEnoughStock() {
        SalesOrder so = orderReserved();
        Carrier carrier = activeCarrier();
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(so));
        when(carrierRepository.findById(2L)).thenReturn(Optional.of(carrier));
        Inventory inv = Inventory.builder().id(800L).product(so.getLines().get(0).getProduct()).warehouse(so.getWarehouse()).qtyOnHand(1).qtyReserved(3).build();
        when(inventoryRepository.findByProductAndWarehouse(any(), any())).thenReturn(Optional.of(inv));
        assertThrows(RuntimeException.class, () -> service.createShipment(req()));
    }
}


