package com.logistechpro.Service;

import com.logistechpro.DTO.Response.ReservationResponse;

public interface ReservationService {
    ReservationResponse reserveOrder(Long orderId);
    ReservationResponse recheckReservation(Long orderId);
}

