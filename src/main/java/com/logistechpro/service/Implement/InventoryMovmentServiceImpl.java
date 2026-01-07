package com.logistechpro.service.Implement;

import com.logistechpro.dto.request.InventoryMovmentRequest;
import com.logistechpro.dto.response.InventoryMovmentResponse;
import com.logistechpro.mapper.InventoryMovmentMapper;
import com.logistechpro.models.Inventory;
import com.logistechpro.models.InventoryMovement;
import com.logistechpro.models.Product;
import com.logistechpro.models.Warehouse;
import com.logistechpro.repository.InventoryMovmentRepository;
import com.logistechpro.repository.InventoryRepository;
import com.logistechpro.repository.ProductRepository;
import com.logistechpro.repository.WarehouseRepository;
import com.logistechpro.service.InventoryMovmentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class InventoryMovmentServiceImpl implements InventoryMovmentService {

    private final InventoryMovmentRepository movementRepo;
    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;
    private final WarehouseRepository warehouseRepo;
    private final InventoryMovmentMapper mapper;

    @Override
    public List<InventoryMovmentResponse> getAll() {
        return movementRepo.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public InventoryMovmentResponse create(InventoryMovmentRequest req) {
        return switch (req.getType()) {
            case INBOUND -> inbound(req);
            case OUTBOUND -> outbound(req);
            case ADJUSTMENT -> adjust(req);
        };
    }

    @Override
    public InventoryMovmentResponse inbound(InventoryMovmentRequest req) {
        var ctx = resolveContext(req);
        // +qtyOnHand
        ctx.inventory.setQtyOnHand(ctx.inventory.getQtyOnHand() + req.getQuantity());
        inventoryRepo.save(ctx.inventory);

        var movement = buildMovement(ctx, req);
        return mapper.toResponse(movementRepo.save(movement));
    }

    @Override
    public InventoryMovmentResponse outbound(InventoryMovmentRequest req) {
        var ctx = resolveContext(req);
        // منع stock négatif
        if (ctx.inventory.getQtyOnHand() < req.getQuantity()) {
            throw new RuntimeException("Not enough stock for OUTBOUND movement");
        }
        ctx.inventory.setQtyOnHand(ctx.inventory.getQtyOnHand() - req.getQuantity());
        inventoryRepo.save(ctx.inventory);

        var movement = buildMovement(ctx, req);
        return mapper.toResponse(movementRepo.save(movement));
    }

    @Override
    public InventoryMovmentResponse adjust(InventoryMovmentRequest req) {
        var ctx = resolveContext(req);
        // ADJUSTMENT ممكن يكون + أو - (ولكن quantity positive عندك، إذن: استعمل الإشارة عبر الوصف أو business rule)
        // هنا نعتمد: adjustment دايماً يزيد qtyOnHand بالـ quantity،
        // إلا بغيت تنقص، بعت request OUTBOUND أو خلي quantity بالسالب وبدّل @Positive → @NotNull.
        ctx.inventory.setQtyOnHand(ctx.inventory.getQtyOnHand() + req.getQuantity());
        // حماية ضد السالب:
        if (ctx.inventory.getQtyOnHand() < 0) {
            throw new RuntimeException("Adjustment would make stock negative");
        }
        inventoryRepo.save(ctx.inventory);

        var movement = buildMovement(ctx, req);
        return mapper.toResponse(movementRepo.save(movement));
    }

    // Helpers
    private record Ctx(Product product, Warehouse warehouse, Inventory inventory) {}

    private Ctx resolveContext(InventoryMovmentRequest req) {
        var product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        var warehouse = warehouseRepo.findById(req.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        var inventory = inventoryRepo.findByProductAndWarehouse(product, warehouse)
                .orElseGet(() -> {
                    // إلا ماكانش inventory، نخلقوه بصفر
                    var inv = new Inventory();
                    inv.setProduct(product);
                    inv.setWarehouse(warehouse);
                    inv.setQtyOnHand(0);
                    inv.setQtyReserved(0);
                    return inventoryRepo.save(inv);
                });
        return new Ctx(product, warehouse, inventory);
    }

    private InventoryMovement buildMovement(Ctx ctx, InventoryMovmentRequest req) {
        return InventoryMovement.builder()
                .product(ctx.product)
                .warehouse(ctx.warehouse)
                .type(req.getType())
                .qty(req.getQuantity())
                .referenceDocument(req.getReferenceDocument())
                .description(req.getDescription())
                .occurredAt(
                        // إلا بغيتي تعتمد اللي جا من الـ request، فعّل سطر تحت و زيد الحقل فـ DTO
                        // req.getOccurredAt() != null ? req.getOccurredAt() : LocalDateTime.now()
                        LocalDateTime.now()
                )
                .build();
    }
}
