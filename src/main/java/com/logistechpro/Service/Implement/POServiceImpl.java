package com.logistechpro.Service.Implement;

import com.logistechpro.DTO.Request.PORequest;
import com.logistechpro.DTO.Response.POResponse;
import com.logistechpro.Mapper.POMapper;
import com.logistechpro.Models.*;
import com.logistechpro.Models.Enums.MovementType;
import com.logistechpro.Models.Enums.PurchaseOrderStatus;
import com.logistechpro.Repository.*;
import com.logistechpro.Service.POService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class POServiceImpl implements POService{

    private final SupplierRepository supplierRepo;
    private final WarehouseRepository warehouseRepo;
    private final ProductRepository productRepo;
    private final PORepository poRepo;
    private final POMapper mapper;
    private final InventoryRepository inventoryRepo;
    private final InventoryMovmentRepository movmentRepo;

    @Override
    public POResponse create(PORequest request) {
        Supplier supplier = supplierRepo.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Warehouse warehouse = warehouseRepo.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        if(request.getLines().isEmpty()) throw new RuntimeException("At least one line is required");

        PurchaseOrder po = PurchaseOrder.builder()
                .supplier(supplier)
                .warehouse(warehouse)
                .status(PurchaseOrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        List<PurchaseOrderLine> lines = request.getLines().stream().map(lineReq -> {
            Product product = productRepo.findById(lineReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            return PurchaseOrderLine.builder()
                    .product(product)
                    .quantity(lineReq.getQuantity())
                    .unitPrice(lineReq.getUnitPrice())
                    .purchaseOrder(po)
                    .build();
        }).toList();

        po.setLines(lines);

        PurchaseOrder saved = poRepo.save(po);
        return mapper.toResponse(saved);
    }

    public POResponse approvePurchaseOrder(Long poId){
        PurchaseOrder po = poRepo.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        if (po.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED orders can be validated");
        }
        po.setStatus(PurchaseOrderStatus.APPROVED);

        PurchaseOrder saved = poRepo.save(po);
        return mapper.toResponse(saved);
    }

    @Transactional
    public POResponse receivePurchaseOrder(Long poId) {
        PurchaseOrder po = poRepo.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        if (po.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new RuntimeException("Only APPROVED orders can be validated");
        }
        po.setStatus(PurchaseOrderStatus.RECEIVED);

        Warehouse warehouse = po.getWarehouse();

        for (PurchaseOrderLine line : po.getLines()) {
            Product product = line.getProduct();

            Inventory inventory = inventoryRepo.findByProductAndWarehouse(product, warehouse).orElse(null);

            if (inventory == null) {
                inventory = Inventory.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .qtyOnHand(0)
                        .qtyReserved(0)
                        .build();
            }
            inventory.setQtyOnHand(inventory.getQtyOnHand() + line.getQuantity());
            inventoryRepo.save(inventory);

            InventoryMovement movement = InventoryMovement.builder()
                    .product(product)
                    .warehouse(warehouse)
                    .type(MovementType.INBOUND)
                    .qty(line.getQuantity())
                    .occurredAt(LocalDateTime.now())
                    .referenceDocument("PO-" + po.getId())
                    .description("Auto reception after PO approval")
                    .build();

            movmentRepo.save(movement);
        }

        PurchaseOrder saved = poRepo.save(po);
        return mapper.toResponse(saved);
    }

    public POResponse cancelPurchaseOrder(Long id){
        PurchaseOrder po = poRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        if (po.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED orders can be canceled");
        }

        po.setStatus(PurchaseOrderStatus.CANCELED);

        PurchaseOrder saved = poRepo.save(po);
        return mapper.toResponse(saved);
    }
}
