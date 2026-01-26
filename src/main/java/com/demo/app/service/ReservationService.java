package com.demo.app.service;

import com.demo.app.entity.*;
import com.demo.app.mapper.ReservationMapper;
import com.demo.app.model.MakeReservationRequest;
import com.demo.app.repository.FlightRepository;
import com.demo.app.repository.PassengerRepository;
import com.demo.app.repository.ReservationRepository;
import com.demo.app.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;

    private final ReservationMapper reservationMapper;

    public void makeReservation(MakeReservationRequest request) {
        Optional<Seat> seat = seatRepository.findById(request.getSeatId());
        Optional<Passenger> passenger = passengerRepository.findById(request.getPassengerId());
        Optional<Flight> flight = flightRepository.findById(request.getFlightId());

        Reservation reservation = Reservation.builder()
                .seat(seat.get())
                .passenger(passenger.get())
                .flight(flight.get())
                .status(ReservationStatus.PENDING)
                .build();

        reservationRepository.save(reservation);
        log.info("Reservation saved to database");
    }

    @Transactional
    public void testHibernateCache() {
        Reservation reservation = reservationRepository.findById(1L).get();

        reservation.setStatus(ReservationStatus.CONFIRMED);

        reservationRepository.save(reservation);

        log.info("Reservation updated in database status: {}", reservation.getStatus());

        Reservation reservation2 = reservationRepository.findById(1L).get();

        reservation2.setStatus(ReservationStatus.CANCELLED);

        reservationRepository.save(reservation2);

        log.info("Reservation fetched from database status: {}", reservation2.getStatus());
    }

    @Transactional
    public void updateReservation() {
        Reservation reservation = reservationRepository.findById(1L).get();

        Flight flight = reservation.getFlight();

        flight.setStatus(FlightStatus.CANCELLED);

        Seat seat = Seat.builder()
                .status(SeatStatus.RESERVED)
                .seatNumber("123123")
                .flight(flight)
                .build();

        flight.getSeats().add(seat);

        reservationRepository.save(reservation);

        log.info("Flight status updated: {}", flight.getStatus());
    }

    @Transactional
    public void updateFlight() {
        Seat seat = seatRepository.findById(20L).get();
        Flight flight = seat.getFlight();
        flight.setStatus(FlightStatus.CANCELLED);

        seatRepository.save(seat);

        log.info("Flight status updated: {}", flight.getStatus());

    }

}
