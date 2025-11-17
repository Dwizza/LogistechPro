package com.logistechpro.Service.Implement;

import com.logistechpro.DTO.Response.ReservationResponse;
import com.logistechpro.Models.*;
import com.logistechpro.Models.Enums.MovementType;
import com.logistechpro.Models.Enums.OrderStatus;
import com.logistechpro.Repository.InventoryMovmentRepository;
import com.logistechpro.Repository.InventoryRepository;
import com.logistechpro.Repository.SalesOrderRepository;
import com.logistechpro.Repository.WarehouseRepository;
import com.logistechpro.Service.ReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final SalesOrderRepository orderRepo;
    private final InventoryRepository inventoryRepo;
    private final InventoryMovmentRepository movementRepo;
    private final WarehouseRepository warehouseRepo;

    @Override
    public ReservationResponse reserveOrder(Long orderId) {
        SalesOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.CREATED)
            throw new RuntimeException("Only CREATED orders can be reserved");

        boolean allReserved = true;
        Map<String, Integer> shortages = new HashMap<>();
        Warehouse mainWarehouse = order.getWarehouse();

        for (SalesOrderLine line : order.getLines()) {
            Product product = line.getProduct();
            int requestedQty = line.getQuantity();

            Inventory inv = inventoryRepo.findByProductAndWarehouse(product, mainWarehouse)
                    .orElseThrow(() -> new RuntimeException("No inventory for " + product.getName()));

            int available = inv.getQtyOnHand() - inv.getQtyReserved();

            if (available >= requestedQty) {
                inv.setQtyReserved(inv.getQtyReserved() + requestedQty);
                inventoryRepo.save(inv);


                line.setQtyReserved(requestedQty);
                line.setQtyShortage(0);
                line.setBackOrder(false);
            }else {
                int reserved = Math.max(0, available);
                inv.setQtyReserved(inv.getQtyReserved() + reserved);
                inventoryRepo.save(inv);

                int shortage = requestedQty - reserved;
                line.setQtyReserved(reserved);
                line.setQtyShortage(shortage);
                line.setBackOrder(true);

                int remainingShortage = shortage;
                boolean foundEnough = false;

                for (Warehouse otherWarehouse : warehouseRepo.findAll()) {
                    if (otherWarehouse.getId().equals(mainWarehouse.getId())) continue;

                    Inventory otherInv = inventoryRepo.findByProductAndWarehouse(product, otherWarehouse)
                            .orElse(null);

                    if (otherInv != null) {
                        int availableOther = otherInv.getQtyOnHand() - otherInv.getQtyReserved();

                        if (availableOther > 0) {
                            int transferQty = Math.min(availableOther, remainingShortage);

                            otherInv.setQtyOnHand(otherInv.getQtyOnHand() - transferQty);
                            inv.setQtyOnHand(inv.getQtyOnHand() + transferQty);
                            inventoryRepo.save(otherInv);
                            inventoryRepo.save(inv);

                            inv.setQtyReserved(inv.getQtyReserved() + transferQty);
                            line.setQtyReserved(line.getQtyReserved() + transferQty);
                            remainingShortage -= transferQty;

                            InventoryMovement out = new InventoryMovement();
                            out.setProduct(product);
                            out.setWarehouse(otherWarehouse);
                            out.setType(MovementType.OUTBOUND);
                            out.setQty(transferQty);
                            out.setOccurredAt(LocalDateTime.now());
                            movementRepo.save(out);

                            InventoryMovement in = new InventoryMovement();
                            in.setProduct(product);
                            in.setWarehouse(mainWarehouse);
                            in.setType(MovementType.INBOUND);
                            in.setQty(transferQty);
                            in.setOccurredAt(LocalDateTime.now());
                            movementRepo.save(in);
                        }

                        if (remainingShortage <= 0) {
                            foundEnough = true;
                            line.setQtyShortage(0);
                            line.setBackOrder(false);
                            break;
                        }
                    }
                }

                if (!foundEnough && remainingShortage > 0) {
                    shortages.put(product.getName(), remainingShortage);
                    allReserved = false;

                    InventoryMovement adjust = new InventoryMovement();
                    adjust.setProduct(product);
                    adjust.setWarehouse(mainWarehouse);
                    adjust.setType(MovementType.ADJUSTMENT);
                    adjust.setQty(remainingShortage);
                    adjust.setOccurredAt(LocalDateTime.now());
                    movementRepo.save(adjust);
                    line.setQtyShortage(remainingShortage);
                    System.out.println(remainingShortage);

                }
            }
        }

        order.setStatus(allReserved ? OrderStatus.RESERVED : OrderStatus.PARTIALLY_RESERVED);

        orderRepo.save(order);

        return new ReservationResponse(order.getId(), order.getStatus().name(), shortages);
    }

    @Override
    public ReservationResponse recheckReservation(Long orderId) {
        SalesOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PARTIALLY_RESERVED)
            throw new RuntimeException("Only PARTIALLY_RESERVED orders can be rechecked");

        boolean allNowReserved = true;
        Map<String, Integer> stillMissing = new HashMap<>();
        Warehouse mainWarehouse = order.getWarehouse();

        for (SalesOrderLine line : order.getLines()) {
            if (line.getQtyShortage() <= 0) continue;

            Product product = line.getProduct();
            int missingQty = line.getQtyShortage();

            Inventory inv = inventoryRepo.findByProductAndWarehouse(product, mainWarehouse)
                    .orElseThrow(() -> new RuntimeException("No inventory for " + product.getName()));

            int available = inv.getQtyOnHand() - inv.getQtyReserved();

            if (available >= missingQty) {
                inv.setQtyReserved(inv.getQtyReserved() + missingQty);
                inventoryRepo.save(inv);

                line.setQtyReserved(line.getQtyReserved() + missingQty);
                line.setQtyShortage(0);
                line.setBackOrder(false);
            } else if (available > 0) {
                inv.setQtyReserved(inv.getQtyReserved() + available);
                inventoryRepo.save(inv);

                line.setQtyReserved(line.getQtyReserved() + available);
                line.setQtyShortage(missingQty - available);
                stillMissing.put(product.getName(), missingQty - available);
                allNowReserved = false;
            } else {
                stillMissing.put(product.getName(), missingQty);
                allNowReserved = false;
            }
        }

        order.setStatus(allNowReserved ? OrderStatus.RESERVED : OrderStatus.PARTIALLY_RESERVED);
        orderRepo.save(order);

        return new ReservationResponse(order.getId(), order.getStatus().name(), stillMissing);
    }
}

