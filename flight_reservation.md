# ✈️ Spring Data JPA – Uçuş Rezervasyon Deneme Projesi

Bu proje, **Spring Data JPA + PostgreSQL** kullanarak **eşzamanlı erişim (concurrency)**, **transaction yönetimi**, **locking mekanizmaları** ve **performans testlerini** gözlemlemek amacıyla hazırlanmış bir **deneme / öğrenme projesidir**.

---

## 🎯 Projenin Amaçları

* Spring Data JPA’nin transactional davranışlarını gözlemlemek
* PostgreSQL MVCC ve row-level locking mekanizmalarını incelemek
* Optimistic vs Pessimistic locking farklarını test etmek
* Aynı kayda eşzamanlı erişimde oluşabilecek problemleri görmek
* Performans ve lock contention etkilerini ölçmek

---

## 🧱 Teknoloji Stack

* Java 17+
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Flyway (opsiyonel)
* Testcontainers (opsiyonel)
* JMeter / Gatling (opsiyonel)

---

## 🗂️ Proje Klasör Yapısı

```text
flight-reservation-demo
│
├── README.md
├── docker-compose.yml
├── pom.xml
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.flight
│   │   │       ├── FlightReservationApplication.java
│   │   │       │
│   │   │       ├── config
│   │   │       │   ├── JpaConfig.java
│   │   │       │   └── TransactionConfig.java
│   │   │       │
│   │   │       ├── controller
│   │   │       │   └── ReservationController.java
│   │   │       │
│   │   │       ├── domain
│   │   │       │   ├── flight
│   │   │       │   │   ├── Flight.java
│   │   │       │   │   ├── FlightStatus.java
│   │   │       │   │   └── FlightRepository.java
│   │   │       │   │
│   │   │       │   ├── seat
│   │   │       │   │   ├── Seat.java
│   │   │       │   │   ├── SeatStatus.java
│   │   │       │   │   └── SeatRepository.java
│   │   │       │   │
│   │   │       │   ├── passenger
│   │   │       │   │   ├── Passenger.java
│   │   │       │   │   └── PassengerRepository.java
│   │   │       │   │
│   │   │       │   └── reservation
│   │   │       │       ├── Reservation.java
│   │   │       │       ├── ReservationStatus.java
│   │   │       │       └── ReservationRepository.java
│   │   │       │
│   │   │       ├── service
│   │   │       │   ├── ReservationService.java
│   │   │       │   ├── OptimisticReservationService.java
│   │   │       │   └── PessimisticReservationService.java
│   │   │       │
│   │   │       ├── event
│   │   │       │   ├── ReservationCreatedEvent.java
│   │   │       │   └── ReservationEventListener.java
│   │   │       │
│   │   │       ├── exception
│   │   │       │   ├── SeatAlreadyReservedException.java
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       │
│   │   │       └── util
│   │   │           └── ConcurrentTestRunner.java
│   │   │
│   │   └── resources
│   │       ├── application.yml
│   │       └── db
│   │           └── migration
│   │               ├── V1__init_tables.sql
│   │               └── V2__add_indexes.sql
│   │
│   └── test
│       ├── java
│       │   └── com.example.flight
│       │       ├── ReservationConcurrencyTest.java
│       │       └── OptimisticLockTest.java
│       │
│       └── resources
│           └── application-test.yml
```

---

## 🧠 Domain Model Özeti

### Flight

* Uçuş bilgisini temsil eder
* Birden fazla koltuğu vardır

### Seat

* Bir uçuşa bağlıdır
* **Concurrency testlerinin ana odağıdır**
* `@Version` alanı ile optimistic locking desteklenir

### Passenger

* Rezervasyon yapan kullanıcı

### Reservation

* Passenger + Seat + Flight ilişkisini temsil eder

---

## 🔐 Locking Senaryoları

### 1️⃣ Lock Yok (Problemli)

* Double booking görülebilir
* Lost update oluşabilir

### 2️⃣ Optimistic Lock

* `@Version` ile JPA kontrol eder
* Exception + retry gerekir

### 3️⃣ Pessimistic Lock

* `SELECT FOR UPDATE`
* DB seviyesinde kesin güvenlik
* Daha yüksek latency

---

## 🔬 Test Senaryoları

* Aynı koltuk için 10 / 100 / 1000 concurrent request
* Başarılı / başarısız rezervasyon sayıları
* Ortalama response süreleri
* Lock wait süreleri

---

## 📊 Performans Ölçüm Araçları

* JMeter / Gatling
* CompletableFuture tabanlı concurrency testleri
* PostgreSQL `pg_locks`, `pg_stat_activity`

---

## 🚀 Genişletme Fikirleri

* Retry with exponential backoff
* Kafka ile async event handling
* Read / Write model ayrımı
* Isolation level karşılaştırmaları
* Deadlock simülasyonları

---

## 🧩 Hedef

Bu proje tamamlandığında:

* Spring Data JPA concurrency davranışlarını **net şekilde anlamış**
* PostgreSQL locking mekanizmalarını **pratikte gözlemlemiş**
* Gerçek hayata yakın bir **rezervasyon problemi çözmüş** olacaksın

---

> Bu doküman, öğrenme ve deneme amaçlıdır. Gerçek prod sistemler için ek güvenlik ve ölçeklenebilirlik önlemleri gereklidir.
