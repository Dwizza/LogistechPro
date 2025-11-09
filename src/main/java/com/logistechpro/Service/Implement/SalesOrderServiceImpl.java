package com.logistechpro.Service.Implement;

import com.logistechpro.DTO.Response.SalesOrderResponse;
import com.logistechpro.DTO.SalesOrderRequest;
import com.logistechpro.Mapper.SalesOrderMapper;
import com.logistechpro.Models.*;
import com.logistechpro.Models.Enums.OrderStatus;
import com.logistechpro.Repository.ClientRepository;
import com.logistechpro.Repository.ProductRepository;
import com.logistechpro.Repository.SalesOrderRepository;
import com.logistechpro.Repository.WarehouseRepository;
import com.logistechpro.Service.SalesOrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {
    private final SalesOrderRepository salesOrderRepository;
    private final ClientRepository clientRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final SalesOrderMapper mapper;

    public SalesOrderResponse create(SalesOrderRequest request) {

        SalesOrder order = mapper.toEntity(request);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        if (order.getLines() == null) {
            order.setLines(new ArrayList<>());
        }

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        order.setClient(client);
        order.setWarehouse(warehouse);

        request.getLines().forEach(lineReq -> {
            Product product = productRepository.findById(lineReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            SalesOrderLine line = mapper.toEntity(lineReq);
            line.setProduct(product);
            line.setSalesOrder(order);
            order.getLines().add(line);
        });

        SalesOrder saved = salesOrderRepository.save(order);
        return mapper.toResponse(saved);
    }

    @Override
    public SalesOrderResponse findById(Long id) {
        return salesOrderRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<SalesOrderResponse> findAll() {
        return salesOrderRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<SalesOrderResponse> findByStatus(String status) {
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }

        return salesOrderRepository.findByStatus(String.valueOf(orderStatus)).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
