package com.demo.app.repository;

import com.demo.app.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    // Seats ile birlikte çek (lazy loading sorunu olmaz)
    @Query("SELECT f FROM Flight f LEFT JOIN FETCH f.seats WHERE f.id = :id")
//    @EntityGraph(attributePaths = "seats")
    Optional<Flight> findByIdWithSeats(@Param("id") Long id);
}
