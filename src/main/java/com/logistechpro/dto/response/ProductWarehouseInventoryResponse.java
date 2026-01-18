package com.logistechpro.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductWarehouseInventoryResponse {
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;

    private int qtyOnHand;
    private int qtyReserved;
    private int qtyAvailable;

    /**
     * Prix du produit (global). Il n'y a pas de prix par warehouse dans le modèle actuel.
     */
    private BigDecimal price;
}

