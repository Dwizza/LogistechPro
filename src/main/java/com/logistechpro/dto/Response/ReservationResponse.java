package com.logistechpro.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {
    private Long orderId;
    private String status;
    private Map<String, Integer> shortages;
}

