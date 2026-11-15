-- EcoNest database schema (SQLite)
-- The application creates these tables by itself on first run (see dao/DatabaseSetup.java).
-- This file is only here as a reference for the ER diagram / documentation.

CREATE TABLE products (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          TEXT    NOT NULL,
    category      TEXT    NOT NULL,          -- PLANT, PLANTER or KIT
    price         REAL    NOT NULL,
    eco_rating    INTEGER NOT NULL,          -- 1 to 5
    spec          TEXT,                      -- light needs / material / kit contents
    description   TEXT,
    quantity      INTEGER NOT NULL DEFAULT 0,
    reorder_level INTEGER NOT NULL DEFAULT 5
);

CREATE TABLE clients (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    name           TEXT NOT NULL,
    type           TEXT NOT NULL,            -- HOUSEHOLD or OFFICE
    contact_person TEXT,                     -- office clients only
    phone          TEXT NOT NULL,
    email          TEXT NOT NULL,
    address        TEXT NOT NULL
);

CREATE TABLE delivery_agents (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    name           TEXT NOT NULL,
    phone          TEXT NOT NULL,
    email          TEXT NOT NULL,
    vehicle_type   TEXT NOT NULL,
    vehicle_number TEXT NOT NULL,
    available      INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE stock_in (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id    INTEGER NOT NULL,
    quantity      INTEGER NOT NULL,
    supplier      TEXT    NOT NULL,
    received_date TEXT    NOT NULL,
    note          TEXT,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE orders (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    client_id        INTEGER NOT NULL,
    order_date       TEXT    NOT NULL,
    status           TEXT    NOT NULL,       -- PENDING, ASSIGNED, DISPATCHED, DELIVERED, CANCELLED
    delivery_address TEXT,
    total            REAL    NOT NULL,
    agent_id         INTEGER,
    delivery_date    TEXT,
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (agent_id)  REFERENCES delivery_agents(id)
);

CREATE TABLE order_items (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id   INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity   INTEGER NOT NULL,
    unit_price REAL    NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE email_log (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    sent_at   TEXT NOT NULL,
    recipient TEXT NOT NULL,
    subject   TEXT NOT NULL,
    status    TEXT NOT NULL
);
