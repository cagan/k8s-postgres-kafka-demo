package com.demo.app.controller;

import com.demo.app.model.MakeReservationRequest;
import com.demo.app.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<String> makeReservation(@RequestBody MakeReservationRequest request) {
        reservationService.makeReservation(request);
        return ResponseEntity.ok("Reservation completed!");
    }

    @GetMapping("/test-cache")
    public ResponseEntity<String> testHibernateCache() {
        reservationService.testHibernateCache();
        return ResponseEntity.ok("Cache updated!");
    }

    @GetMapping("/update-reservation")
    public ResponseEntity<String> updateReservation() {
        reservationService.updateReservation();
        return ResponseEntity.ok("reservation updated!");
    }

    @GetMapping("/update-flight")
    public ResponseEntity<String> updateFlight() {
        reservationService.updateFlight();
        return ResponseEntity.ok("flight updated!");
    }
}
