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

    @Transactional
    public POResponse validatePurchaseOrder(Long poId) {
        // 1️⃣ نجيب الـ PO
        PurchaseOrder po = poRepo.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        if (po.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED orders can be validated");
        }

        // 2️⃣ نغيّر الحالة
        po.setStatus(PurchaseOrderStatus.APPROVED);

        Warehouse warehouse = po.getWarehouse();

        // 3️⃣ نمرّ على كل Line فـ PO
        for (PurchaseOrderLine line : po.getLines()) {
            Product product = line.getProduct();

            // 🔹 نجيب Inventory ديال المنتج فهاد المخزن
            Inventory inventory = inventoryRepo.findByProductAndWarehouse(product, warehouse)
                    .orElseGet(() -> Inventory.builder()
                            .product(product)
                            .warehouse(warehouse)
                            .qtyOnHand(0)
                            .qtyReserved(0)
                            .build()
                    );

            // 🔹 نحدّث الكمية
            inventory.setQtyOnHand(inventory.getQtyOnHand() + line.getQuantity());
            inventoryRepo.save(inventory);

            // 4️⃣ نسجّل حركة جديدة (InventoryMovement)
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

        // 5️⃣ نحفظ التغييرات فالـ PO
        PurchaseOrder saved = poRepo.save(po);
        return mapper.toResponse(saved);
    }
}
