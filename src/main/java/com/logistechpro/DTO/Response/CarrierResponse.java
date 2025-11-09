package com.logistechpro.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarrierResponse {
    private Long id;
    private String name;
    private String contactEmail;
    private BigDecimal capacityWeight;
    private BigDecimal capacityVolume;
    private boolean active;
    private String status;
}
