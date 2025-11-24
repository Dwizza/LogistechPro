package com.logistechpro.dto.Response;

import lombok.Data;

@Data
public class WarehouseResponse {
    private Long id;
    private String code;
    private String name;
    private boolean active;
}
