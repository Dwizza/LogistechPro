package com.logistechpro.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationRequest {
    @NotNull
    private Long orderId;
}

