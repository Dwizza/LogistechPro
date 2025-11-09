package com.logistechpro.Services.Impl;

import com.logistechpro.DTO.Request.ShipmentRequest;
import com.logistechpro.DTO.Response.ShipmentResponse;
import com.logistechpro.Mapper.ShipmentMapper;
import com.logistechpro.Models.*;
import com.logistechpro.Models.Enums.*;
import com.logistechpro.Repository.*;
import com.logistechpro.Service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CarrierRepository carrierRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovmentRepository inventoryMovementRepository;
    private final ShipmentMapper shipmentMapper;

    @Override
    public ShipmentResponse createShipment(ShipmentRequest request) {
        SalesOrder salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new RuntimeException("Sales order not found."));

        if (salesOrder.getStatus() != OrderStatus.RESERVED) {
            throw new RuntimeException("Sales order must be RESERVED before shipment.");
        }

        Carrier carrier = carrierRepository.findById(request.getCarrierId())
                .orElseThrow(() -> new RuntimeException("Carrier not found."));
        if (carrier.getStatus() != CarrierStatus.ACTIVE) {
            throw new RuntimeException("Carrier must be ACTIVE to create a shipment.");
        }

        Shipment shipment = Shipment.builder()
                .carrier(carrier)
                .salesOrder(salesOrder)
                .trackingNumber(UUID.randomUUID().toString().substring(0, 10))
                .status(ShipmentStatus.PLANNED)
                .plannedDate(LocalDateTime.now())
                .build();
        shipmentRepository.save(shipment);

        for (SalesOrderLine line : salesOrder.getLines()) {
            Inventory inventory = inventoryRepository.findByProductAndWarehouse(
                            line.getProduct(), salesOrder.getWarehouse())
                    .orElseThrow(() -> new RuntimeException(
                            "No inventory found for product " + line.getProduct().getName()));

            int orderedQty = line.getQuantity();

            if (inventory.getQtyOnHand() < orderedQty) {
                throw new RuntimeException("Not enough stock for " + line.getProduct().getSku());
            }

            inventory.setQtyOnHand(inventory.getQtyOnHand() - orderedQty);
            inventory.setQtyReserved(inventory.getQtyReserved() - orderedQty);
            inventoryRepository.save(inventory);

            InventoryMovement movement = InventoryMovement.builder()
                    .product(line.getProduct())
                    .warehouse(salesOrder.getWarehouse())
                    .type(MovementType.OUTBOUND)
                    .qty(orderedQty)
                    .occurredAt(LocalDateTime.now())
                    .referenceDocument("SHIPMENT-" + shipment.getId())
                    .build();
            inventoryMovementRepository.save(movement);
        }

        salesOrder.setStatus(OrderStatus.SHIPPED);
        salesOrderRepository.save(salesOrder);

        return shipmentMapper.toResponse(shipment);
    }
}
