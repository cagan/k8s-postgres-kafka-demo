# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Spring Boot demo project for learning **Hibernate transaction management**, **concurrent access patterns**, and **Kubernetes orchestration** through a flight reservation system.

**Tech Stack:** Java 21, Spring Boot 4.0.1, PostgreSQL 16, Kafka, OpenTelemetry, Kubernetes

## Build & Run Commands

```bash
# Build
mvn clean package

# Run locally (requires infra running)
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Start infrastructure only (postgres + kafka + monitoring)
skaffold dev -p infra

# Full Kubernetes deployment
skaffold dev

# Build Docker image
mvn jib:build -Dimage=demo-app:latest
```

## Architecture

### Domain Model
```
Passenger 1---* Reservation *---1 Seat *---1 Flight
```

- **Flight**: flight_number, origin, destination, status (SCHEDULED/DELAYED/BOARDING/DEPARTED/CANCELLED)
- **Seat**: seat_number, status (AVAILABLE/RESERVED/OCCUPIED/BLOCKED), `@Version` for optimistic locking
- **Passenger**: contact info, linked to reservations
- **Reservation**: links passenger + seat + flight, status (PENDING/CONFIRMED/CANCELLED)

### Key Patterns

**Concurrency Control**: Seat entity uses `@Version` for optimistic locking to prevent double-booking.

**Hibernate Features Tested** (via `/test/hibernate/*` endpoints):
- Dirty checking (managed vs detached entities)
- Cascade operations (Flight → Seats)
- Orphan removal
- LazyInitializationException scenarios
- JOIN FETCH queries (`FlightRepository.findByIdWithSeats()`)

**Kafka Integration**: Producer/consumer for "messages" topic with Kafdrop UI monitoring.

### Project Structure

```
src/main/java/com/demo/app/
├── controller/     # REST endpoints (Reservation, Message, HibernateTest)
├── service/        # Business logic
├── entity/         # JPA entities with relationships
├── repository/     # Spring Data JPA repos
├── mapper/         # MapStruct DTOs
├── config/         # Kafka, DataSource (OTel JDBC wrapping)
└── exception/      # GlobalExceptionHandler (RFC 7807)

src/main/resources/
├── application.yml          # Default config
├── application-local.yml    # Local dev with LGTM stack
└── db/migration/            # Flyway migrations (V1, V2)

k8s/
├── app/       # Spring Boot deployment
├── postgres/  # PostgreSQL with PVC
├── kafka/     # Kafka + Zookeeper
├── kafdrop/   # Kafka UI
└── otel-lgtm/ # Observability stack (Grafana, Tempo, Loki)
```

## API Endpoints

**Reservations**: `POST /api/reservations`, various test endpoints for cache/update behavior

**Kafka Messages**: `POST /api/messages/kafka` (send), `GET /api/messages` (list)

**Hibernate Testing**: `POST /test/hibernate/{scenario}` - dirty-checking, cascade, orphan-removal, lazy-exception

## Database

Flyway manages schema. Migrations in `src/main/resources/db/migration/`:
- V1: Creates tables (passengers, flights, seats, reservations)
- V2: Sample data (5 passengers, 3 flights with 6 seats each)

Connection pool: HikariCP (10 max, 2000ms leak detection)

## Local Development

```bash
# Terminal 1: Infrastructure
skaffold dev -p infra

# Terminal 2: Application
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Ports**: App (8080), PostgreSQL (5432), Kafka (9092), Kafdrop (9000), Grafana (3000)

## Observability

OpenTelemetry configured for:
- JDBC query tracing via `JdbcTelemetry` wrapper
- Log correlation via Logback appender
- OTLP export to LGTM stack (local profile uses 100% sampling)
