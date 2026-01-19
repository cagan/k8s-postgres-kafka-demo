-- Sample Passengers
INSERT INTO passengers (first_name, last_name, email, phone_number) VALUES
    ('Ahmet', 'Yilmaz', 'ahmet.yilmaz@email.com', '+90 532 111 2233'),
    ('Mehmet', 'Kaya', 'mehmet.kaya@email.com', '+90 533 222 3344'),
    ('Ayse', 'Demir', 'ayse.demir@email.com', '+90 534 333 4455'),
    ('Fatma', 'Celik', 'fatma.celik@email.com', '+90 535 444 5566'),
    ('Ali', 'Ozturk', 'ali.ozturk@email.com', '+90 536 555 6677');

-- Sample Flights
INSERT INTO flights (flight_number, origin, destination, departure_time, arrival_time, status) VALUES
    ('TK101', 'Istanbul', 'Ankara', '2025-02-01 08:00:00', '2025-02-01 09:15:00', 'SCHEDULED'),
    ('TK202', 'Istanbul', 'Izmir', '2025-02-01 10:30:00', '2025-02-01 11:45:00', 'SCHEDULED'),
    ('TK303', 'Ankara', 'Antalya', '2025-02-01 14:00:00', '2025-02-01 15:30:00', 'SCHEDULED');

-- Sample Seats for TK101 (Flight ID: 1)
INSERT INTO seats (seat_number, status, flight_id, version) VALUES
    ('1A', 'AVAILABLE', 1, 0),
    ('1B', 'AVAILABLE', 1, 0),
    ('1C', 'AVAILABLE', 1, 0),
    ('2A', 'AVAILABLE', 1, 0),
    ('2B', 'AVAILABLE', 1, 0),
    ('2C', 'AVAILABLE', 1, 0);

-- Sample Seats for TK202 (Flight ID: 2)
INSERT INTO seats (seat_number, status, flight_id, version) VALUES
    ('1A', 'AVAILABLE', 2, 0),
    ('1B', 'AVAILABLE', 2, 0),
    ('1C', 'AVAILABLE', 2, 0),
    ('2A', 'AVAILABLE', 2, 0),
    ('2B', 'AVAILABLE', 2, 0),
    ('2C', 'AVAILABLE', 2, 0);

-- Sample Seats for TK303 (Flight ID: 3)
INSERT INTO seats (seat_number, status, flight_id, version) VALUES
    ('1A', 'AVAILABLE', 3, 0),
    ('1B', 'AVAILABLE', 3, 0),
    ('1C', 'AVAILABLE', 3, 0),
    ('2A', 'AVAILABLE', 3, 0),
    ('2B', 'AVAILABLE', 3, 0),
    ('2C', 'AVAILABLE', 3, 0);