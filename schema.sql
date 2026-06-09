-- ============================================================
-- Karuṇā — Database schema (PostgreSQL)
-- This mirrors the Spring Boot JPA entity definitions
-- ============================================================

CREATE TABLE IF NOT EXISTS app_user (
    id          SERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    phone       VARCHAR(50),
    role        VARCHAR(50) NOT NULL,
    ngo_name    VARCHAR(255),
    available   BOOLEAN DEFAULT TRUE,
    latitude    DOUBLE PRECISION,
    longitude   DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS animal_case (
    id                  SERIAL PRIMARY KEY,
    reporter_id         INTEGER REFERENCES app_user(id),
    responder_id        INTEGER REFERENCES app_user(id),
    image_data_url      TEXT,
    location_label      VARCHAR(500),
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,
    species             VARCHAR(100),
    injury_type         VARCHAR(100),
    severity            VARCHAR(50),
    status              VARCHAR(50) DEFAULT 'reported',
    probable_condition  TEXT,
    first_aid_steps     TEXT DEFAULT '[]',
    estimated_cost_inr  DOUBLE PRECISION DEFAULT 0,
    notes               TEXT DEFAULT '[]',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS donation (
    id                  SERIAL PRIMARY KEY,
    case_id             INTEGER NOT NULL REFERENCES animal_case(id),
    donor_name          VARCHAR(255) NOT NULL,
    amount_inr          DOUBLE PRECISION NOT NULL,
    message             TEXT,
    payment_method      VARCHAR(100) DEFAULT 'UPI',
    bill_offset_details VARCHAR(500),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS adoption_application (
    id              SERIAL PRIMARY KEY,
    case_id         INTEGER NOT NULL REFERENCES animal_case(id),
    applicant_name  VARCHAR(255) NOT NULL,
    contact         VARCHAR(255),
    reason          TEXT,
    status          VARCHAR(50) DEFAULT 'pending',
    adopter_id_url  TEXT,
    checkins_logs   TEXT DEFAULT '[]',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
