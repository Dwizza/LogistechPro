package com.logistechpro.service;

import com.logistechpro.dto.Request.SalesOrderLineRequest;
import com.logistechpro.dto.Response.SalesOrderResponse;
import com.logistechpro.dto.SalesOrderRequest;
import com.logistechpro.mapper.SalesOrderMapper;
import com.logistechpro.models.*;
import com.logistechpro.models.Enums.OrderStatus;
import com.logistechpro.models.Enums.Role;
import com.logistechpro.repository.ClientRepository;
import com.logistechpro.repository.ProductRepository;
import com.logistechpro.repository.SalesOrderRepository;
import com.logistechpro.repository.WarehouseRepository;
import com.logistechpro.service.Implement.SalesOrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {
    @Mock SalesOrderRepository salesOrderRepository;
    @Mock ClientRepository clientRepository;
    @Mock WarehouseRepository warehouseRepository;
    @Mock ProductRepository productRepository;
    @Mock SalesOrderMapper mapper;
    @InjectMocks SalesOrderServiceImpl service;

    SalesOrderRequest req() {
        SalesOrderLineRequest line = SalesOrderLineRequest.builder().productId(11L).quantity(3).unitPrice(BigDecimal.TEN).build();
        return SalesOrderRequest.builder().clientId(7L).warehouseId(8L).lines(List.of(line)).build();
    }

    @Test
    void create_success() {
        SalesOrderRequest r = req();
        SalesOrder orderEntity = SalesOrder.builder().id(1L).status(OrderStatus.CREATED).createdAt(LocalDateTime.now()).lines(new java.util.ArrayList<>()).build();
        when(mapper.toEntity(r)).thenReturn(orderEntity);
        Client client = Client.builder().id(7L).telephone("0").address("A").user(User.builder().id(55L).name("C").email("c@x.com").passwordHash("p").role(Role.CLIENT).active(true).build()).build();
        Warehouse wh = Warehouse.builder().id(8L).code("W1").name("Main").build();
        Product prod = Product.builder().id(11L).sku("SKU").name("P").avgPrice(BigDecimal.TEN).active(true).build();
        when(clientRepository.findById(7L)).thenReturn(Optional.of(client));
        when(warehouseRepository.findById(8L)).thenReturn(Optional.of(wh));
        when(productRepository.findById(11L)).thenReturn(Optional.of(prod));
        SalesOrderLine lineEntity = SalesOrderLine.builder().id(90L).product(prod).quantity(3).unitPrice(BigDecimal.TEN).salesOrder(orderEntity).build();
        when(mapper.toEntity(any(SalesOrderLineRequest.class))).thenReturn(lineEntity);
        when(salesOrderRepository.save(orderEntity)).thenReturn(orderEntity);
        SalesOrderResponse resp = SalesOrderResponse.builder().id(1L).status("CREATED").clientName("C").warehouseName("Main").build();
        when(mapper.toResponse(orderEntity)).thenReturn(resp);
        SalesOrderResponse out = service.create(r);
        assertEquals(1L,out.getId());
        assertEquals("CREATED", out.getStatus());
        verify(salesOrderRepository).save(orderEntity);
    }

    @Test
    void create_clientNotFound() {
        SalesOrderRequest r = req();
        SalesOrder orderEntity = SalesOrder.builder().id(1L).build();
        when(mapper.toEntity(r)).thenReturn(orderEntity);
        when(clientRepository.findById(7L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.create(r));
    }

    @Test
    void create_warehouseNotFound() {
        SalesOrderRequest r = req();
        SalesOrder orderEntity = SalesOrder.builder().id(1L).build();
        when(mapper.toEntity(r)).thenReturn(orderEntity);
        Client client = Client.builder().id(7L).user(User.builder().id(55L).name("C").email("c@x.com").passwordHash("p").role(Role.CLIENT).active(true).build()).telephone("0").address("A").build();
        when(clientRepository.findById(7L)).thenReturn(Optional.of(client));
        when(warehouseRepository.findById(8L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.create(r));
    }

    @Test
    void create_productNotFound() {
        SalesOrderRequest r = req();
        SalesOrder orderEntity = SalesOrder.builder().id(1L).build();
        when(mapper.toEntity(r)).thenReturn(orderEntity);
        Client client = Client.builder().id(7L).user(User.builder().id(55L).name("C").email("c@x.com").passwordHash("p").role(Role.CLIENT).active(true).build()).telephone("0").address("A").build();
        Warehouse wh = Warehouse.builder().id(8L).code("W1").name("Main").build();
        when(clientRepository.findById(7L)).thenReturn(Optional.of(client));
        when(warehouseRepository.findById(8L)).thenReturn(Optional.of(wh));
        when(productRepository.findById(11L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.create(r));
    }

    @Test
    void findById_found() {
        SalesOrder order = SalesOrder.builder().id(5L).status(OrderStatus.CREATED).createdAt(LocalDateTime.now()).build();
        when(salesOrderRepository.findById(5L)).thenReturn(Optional.of(order));
        SalesOrderResponse resp = SalesOrderResponse.builder().id(5L).status("CREATED").build();
        when(mapper.toResponse(order)).thenReturn(resp);
        SalesOrderResponse out = service.findById(5L);
        assertEquals(5L,out.getId());
    }

    @Test
    void findById_notFound() {
        when(salesOrderRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.findById(5L));
    }

    @Test
    void findAll() {
        SalesOrder order = SalesOrder.builder().id(1L).status(OrderStatus.CREATED).createdAt(LocalDateTime.now()).build();
        when(salesOrderRepository.findAll()).thenReturn(List.of(order));
        SalesOrderResponse resp = SalesOrderResponse.builder().id(1L).status("CREATED").build();
        when(mapper.toResponse(order)).thenReturn(resp);
        List<SalesOrderResponse> out = service.findAll();
        assertEquals(1,out.size());
    }

    @Test
    void findByStatus_valid() {
        SalesOrder order = SalesOrder.builder().id(1L).status(OrderStatus.CREATED).createdAt(LocalDateTime.now()).build();
        when(salesOrderRepository.findByStatus("CREATED")).thenReturn(List.of(order));
        when(mapper.toResponse(order)).thenReturn(SalesOrderResponse.builder().id(1L).status("CREATED").build());
        List<SalesOrderResponse> out = service.findByStatus("created");
        assertEquals(1,out.size());
        verify(salesOrderRepository).findByStatus("CREATED");
    }

    @Test
    void findByStatus_invalid() {
        assertThrows(RuntimeException.class, () -> service.findByStatus("x"));
    }
}
