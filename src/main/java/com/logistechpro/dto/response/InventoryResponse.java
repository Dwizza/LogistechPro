package com.logistechpro.dto.response;

import lombok.Data;

@Data
public class InventoryResponse {
    private Long id;
    private String productName;
    private String warehouseName;
    private int qtyOnHand;
    private int qtyReserved;
    private int available;
}