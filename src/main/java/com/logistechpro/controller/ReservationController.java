package com.logistechpro.controller;

import com.logistechpro.DTO.Response.ReservationResponse;
import com.logistechpro.Service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ReservationResponse> reserve(@PathVariable Long orderId) {
        return ResponseEntity.ok(reservationService.reserveOrder(orderId));
    }

    @PostMapping("/{orderId}/recheck")
    public ResponseEntity<ReservationResponse> recheck(@PathVariable Long orderId) {
        return ResponseEntity.ok(reservationService.recheckReservation(orderId));
    }
}

