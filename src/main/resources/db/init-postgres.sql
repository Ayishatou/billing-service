-- PostgreSQL Database Initialization Script
-- Run this script to create the database and user for production

-- Create database (run as postgres superuser)
CREATE DATABASE billingdb;

-- Create user
CREATE USER billing_user WITH ENCRYPTED PASSWORD 'change_me_in_production';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE billingdb TO billing_user;

-- Connect to billingdb
\\c billingdb

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO billing_user;

-- Create invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    amount NUMERIC(10,2) NOT NULL CHECK (amount > 0),
    description VARCHAR(500) NOT NULL,
    date_emission DATE NOT NULL,
    date_paiement DATE,
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'PAID', 'CANCELLED')),
    CONSTRAINT chk_payment_method CHECK (payment_method IN ('CARD', 'TRANSFER', 'CASH'))
);

-- Create indexes for better performance
CREATE INDEX idx_invoices_client_id ON invoices(client_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_date_emission ON invoices(date_emission);

-- Sample data (optional, for testing)
INSERT INTO invoices (client_id, amount, description, date_emission, status, payment_method)
VALUES 
    (100, 1500.00, 'Service de consultation IT', CURRENT_DATE - INTERVAL '30 days', 'PAID', 'CARD'),
    (100, 2500.00, 'Developpement application web', CURRENT_DATE - INTERVAL '15 days', 'PENDING', 'TRANSFER'),
    (200, 800.00, 'Maintenance serveur', CURRENT_DATE - INTERVAL '10 days', 'PAID', 'TRANSFER'),
    (200, 1200.00, 'Formation equipe', CURRENT_DATE - INTERVAL '5 days', 'PENDING', 'CARD');
