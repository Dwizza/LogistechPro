package com.logistechpro.service;

import com.logistechpro.dto.response.ReservationResponse;

public interface ReservationService {
    ReservationResponse reserveOrder(Long orderId);
    ReservationResponse recheckReservation(Long orderId);
}

