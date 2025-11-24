package com.logistechpro.service;

import com.logistechpro.dto.Response.ReservationResponse;

public interface ReservationService {
    ReservationResponse reserveOrder(Long orderId);
    ReservationResponse recheckReservation(Long orderId);
}

