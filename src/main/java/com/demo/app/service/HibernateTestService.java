package com.demo.app.service;

import com.demo.app.entity.*;
import com.demo.app.repository.FlightRepository;
import com.demo.app.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HibernateTestService {

    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;

    // ==================== DIRTY CHECKING ====================

    /**
     * Senaryo 1: save() ÇAĞIRMADAN entity değişikliği
     * Hibernate managed entity'yi otomatik takip eder
     */
    @Transactional
    public void dirtyCheckingWithoutSave() {
        log.info("=== DIRTY CHECKING WITHOUT SAVE ===");

        Flight flight = flightRepository.findById(1L).orElseThrow();
        log.info("Flight loaded. Status: {}", flight.getStatus());

        // save() çağırmıyoruz ama değişiklik yapıyoruz
        flight.setStatus(FlightStatus.DELAYED);
        log.info("Status changed to DELAYED (no save called)");
    }

    /**
     * Senaryo 2: save() İLE entity değişikliği
     * Managed entity için save() aslında gereksiz
     */
    @Transactional
    public void dirtyCheckingWithSave() {
        log.info("=== DIRTY CHECKING WITH SAVE ===");

        Flight flight = flightRepository.findById(1L).orElseThrow();
        log.info("Flight loaded. Status: {}", flight.getStatus());

        flight.setStatus(FlightStatus.BOARDING);

        // Bu save() aslında gereksiz - entity zaten managed
        flightRepository.save(flight);
        log.info("save() called - but it's redundant for managed entity");

        log.info("Method ending - transaction will commit...");
    }

    /**
     * Senaryo 3: Transaction OLMADAN entity değişikliği
     * Dirty checking ÇALIŞMAZ - değişiklik kaybolur
     */
    public void dirtyCheckingWithoutTransaction() {
        log.info("=== DIRTY CHECKING WITHOUT TRANSACTION ===");

        // findById kendi mini-transaction'ında çalışır ve biter
        Flight flight = flightRepository.findById(1L).orElseThrow();
        log.info("Flight loaded. Status: {}", flight.getStatus());

        // Entity artık DETACHED - değişiklik takip edilmez
        flight.setStatus(FlightStatus.CANCELLED);
        log.info("Status changed to CANCELLED (but entity is DETACHED!)");

        log.info("Method ending - NO UPDATE will happen!");
    }

    /**
     * Senaryo 4: Detached entity'yi save() ile kaydetme
     */
    public void saveDetachedEntity() {
        log.info("=== SAVE DETACHED ENTITY ===");

        // Entity DETACHED durumda
        Flight flight = flightRepository.findById(1L).orElseThrow();
        log.info("Flight loaded (detached). Status: {}", flight.getStatus());

        flight.setStatus(FlightStatus.SCHEDULED);
        log.info("Status changed to SCHEDULED");

        // save() detached entity için MERGE işlemi yapar
        flightRepository.save(flight);
        log.info("save() called - this triggers MERGE for detached entity");
    }

    // ==================== CASCADE ====================

    /**
     * Senaryo 5a: CASCADE + @Transactional (MANAGED)
     * save() çağırmadan dirty checking ile INSERT
     */
    @Transactional
    public void cascadeInsertManaged() {
        log.info("=== CASCADE INSERT (MANAGED) ===");

        Flight flight = flightRepository.findById(1L).orElseThrow();
        log.info("Flight loaded (MANAGED). Seats: {}", flight.getSeats().size());

        Seat newSeat = Seat.builder()
                .seatNumber("99Z")
                .status(SeatStatus.AVAILABLE)
                .flight(flight)
                .build();

        flight.getSeats().add(newSeat);
        log.info("Seat added - NO save() called");

        // save() YOK - dirty checking + cascade ile INSERT olacak
    }

    /**
     * Senaryo 5b: CASCADE + DETACHED (JOIN FETCH ile)
     * @Transactional yok, save() ile MERGE
     */
    public void cascadeInsertDetached() {
        log.info("=== CASCADE INSERT (DETACHED) ===");

        // JOIN FETCH ile seats eager yüklenir - LazyInitializationException olmaz
        Flight flight = flightRepository.findByIdWithSeats(1L).orElseThrow();
        log.info("Flight loaded (DETACHED with seats). Seats: {}", flight.getSeats().size());

        Seat newSeat = Seat.builder()
                .seatNumber("88X")
                .status(SeatStatus.AVAILABLE)
                .flight(flight)
                .build();

        flight.getSeats().add(newSeat);
        log.info("Seat added to detached flight");

        // DETACHED olduğu için save() ŞART - MERGE + CASCADE
        flightRepository.save(flight);
        log.info("save() called - MERGE triggered CASCADE INSERT");
    }

    /**
     * Senaryo 5c: DETACHED + Lazy = LazyInitializationException
     */
    public void cascadeInsertLazyException() {
        log.info("=== CASCADE INSERT (LAZY EXCEPTION) ===");

        // Normal findById - seats LAZY
        Flight flight = flightRepository.findById(1L).orElseThrow();
        log.info("Flight loaded (DETACHED, seats not loaded)");

        // 💥 Bu satır LazyInitializationException fırlatır
        int seatCount = flight.getSeats().size();
        log.info("Seat count: {}", seatCount);  // Buraya ulaşamaz
    }

    /**
     * Senaryo 6: CASCADE OLMADAN yeni Seat ekleme girişimi
     * Bu senaryo için Seat'i sadece seatRepository ile kaydetmeye çalışıyoruz
     */
    @Transactional
    public void insertWithoutCascade() {
        log.info("=== INSERT WITHOUT CASCADE ===");

        Flight flight = flightRepository.findById(1L).orElseThrow();

        Seat newSeat = Seat.builder()
                .seatNumber("88Y")
                .status(SeatStatus.AVAILABLE)
                .flight(flight)  // İlişkiyi kuruyoruz
                .build();

        // Direkt seatRepository ile kaydediyoruz
        seatRepository.save(newSeat);
        log.info("Seat saved directly via seatRepository");

        // Ama flight.getSeats()'e eklemedik - koleksiyon tutarsız olabilir
        log.info("Flight seats count (may be stale): {}", flight.getSeats().size());
    }

    /**
     * Senaryo 7: orphanRemoval ile Seat silme
     * Flight'tan Seat çıkardığımızda otomatik DELETE olur
     */
    @Transactional
    public void orphanRemoval() {
        log.info("=== ORPHAN REMOVAL ===");

        Flight flight = flightRepository.findById(1L).orElseThrow();
        log.info("Flight loaded. Seat count: {}", flight.getSeats().size());

        if (!flight.getSeats().isEmpty()) {
            Seat seatToRemove = flight.getSeats().get(0);
            log.info("Removing seat: {} (id: {})", seatToRemove.getSeatNumber(), seatToRemove.getId());

            // Koleksiyondan çıkarıyoruz - DELETE çağırmıyoruz
            flight.getSeats().remove(seatToRemove);

            // orphanRemoval = true sayesinde otomatik DELETE olacak
            log.info("Seat removed from collection - orphanRemoval will DELETE it...");
        }
    }

    /**
     * Senaryo 8: CASCADE UPDATE - Flight güncellenince Seat'ler de güncellenir mi?
     */
    @Transactional
    public void cascadeUpdate() {
        log.info("=== CASCADE UPDATE ===");

        Flight flight = flightRepository.findById(1L).orElseThrow();

        // Flight'ı güncelle
        flight.setStatus(FlightStatus.DEPARTED);
        log.info("Flight status changed to DEPARTED");

        // Seat'leri de güncelle
        for (Seat seat : flight.getSeats()) {
            seat.setStatus(SeatStatus.RESERVED);
            log.info("Seat {} status changed to RESERVED", seat.getSeatNumber());
        }

        // Hiçbir save() çağırmıyoruz
        // Hem Flight hem Seat'ler UPDATE edilecek
        log.info("Method ending - both Flight and Seats will be UPDATED...");
    }

    // ==================== N+1 QUERY PROBLEM ====================

    /**
     * Senaryo 9: N+1 Query Problem
     *
     * PROBLEM: findAll() ile tüm Flight'ları çekiyoruz (1 query)
     * Sonra her Flight için seats koleksiyonuna eriştiğimizde LAZY loading tetiklenir (N query)
     * Toplamda: 1 + N query çalışır
     *
     * Örnek: 3 Flight varsa -> 1 Flight query + 3 Seat query = 4 query
     *
     * ÖNEMLİ: spring.jpa.show-sql=true yapın veya OpenTelemetry loglarına bakın
     */
    @Transactional
    public void nPlusOneProblem() {
        log.info("=== N+1 QUERY PROBLEM ===");

        // 1. QUERY: SELECT * FROM flights
        log.info("Step 1: Loading all flights...");
        var flights = flightRepository.findAll();
        log.info("Loaded {} flights", flights.size());

        // N QUERIES: Her flight için ayrı SELECT * FROM seats WHERE flight_id = ?
        log.info("Step 2: Accessing seats for each flight (watch for N additional queries)...");
        for (Flight flight : flights) {
            // Bu satır LAZY loading tetikler - her biri için ayrı query!
            int seatCount = flight.getSeats().size();
            log.info("Flight {} ({}) has {} seats [LAZY LOAD - triggers separate query!]",
                    flight.getFlightNumber(),
                    flight.getDestination(),
                    seatCount);
        }

        log.info("=== TOTAL: 1 Flight query + {} Seat queries = {} queries ===",
                flights.size(),
                flights.size() + 1);
        log.info("Check your SQL logs - you should see {} separate SELECT statements!",
                flights.size() + 1);
    }

    /**
     * Senaryo 10: N+1 Problem Çözümü - JOIN FETCH
     *
     * SOLUTION: LEFT JOIN FETCH ile Flight ve Seat'leri TEK QUERY'de çekiyoruz
     * Toplamda: 1 query
     *
     * Örnek: 3 Flight varsa -> 1 query (Flight + Seats hepsi birlikte)
     *
     * DİKKAT: DISTINCT kullanmamız gerekiyor çünkü JOIN duplicate Flight satırları üretir
     */
    @Transactional
    public void nPlusOneSolution() {
        log.info("=== N+1 SOLUTION: JOIN FETCH ===");

        // TEK QUERY: SELECT f.*, s.* FROM flights f LEFT JOIN seats s ON f.id = s.flight_id
        log.info("Step 1: Loading all flights WITH seats using JOIN FETCH...");
        var flights = flightRepository.findAllWithSeats();
        log.info("Loaded {} flights with all seats in a SINGLE query", flights.size());

        // NO ADDITIONAL QUERIES: Seats zaten yüklenmiş - lazy loading yok
        log.info("Step 2: Accessing seats (NO additional queries - already loaded)...");
        for (Flight flight : flights) {
            // Bu satır artık query tetiklemiyor - seats zaten memory'de!
            int seatCount = flight.getSeats().size();
            log.info("Flight {} ({}) has {} seats [ALREADY LOADED - no query!]",
                    flight.getFlightNumber(),
                    flight.getDestination(),
                    seatCount);
        }

        log.info("=== TOTAL: 1 query (Flight + Seats together) ===");
        log.info("Check your SQL logs - you should see ONLY 1 SELECT statement with JOIN!");
    }

    // ==================== HELPER ====================

    /**
     * Test için Flight durumunu sıfırla
     */
    @Transactional
    public void resetFlightStatus() {
        Flight flight = flightRepository.findById(1L).orElseThrow();
        flight.setStatus(FlightStatus.SCHEDULED);

        for (Seat seat : flight.getSeats()) {
            seat.setStatus(SeatStatus.AVAILABLE);
        }

        log.info("Flight and seats reset to initial status");
    }
}
