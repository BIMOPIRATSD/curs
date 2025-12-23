DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS shipments CASCADE;
DROP TABLE IF EXISTS processing_stages CASCADE;
DROP TABLE IF EXISTS receipts CASCADE;
DROP TABLE IF EXISTS storage_cells CASCADE;
DROP TABLE IF EXISTS waste_types CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'operator', 'viewer'))
);

CREATE TABLE suppliers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type CHAR(1) NOT NULL CHECK (type IN ('F', 'L')),
    phone VARCHAR(50),
    email VARCHAR(100)
);

CREATE TABLE waste_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    density_kg_per_m3 NUMERIC(10, 2),
    recyclable BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE storage_cells (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    max_capacity_kg NUMERIC(10, 2) NOT NULL,
    current_load_kg NUMERIC(10, 2) NOT NULL DEFAULT 0
);

CREATE TABLE receipts (
    id SERIAL PRIMARY KEY,
    supplier_id INTEGER NOT NULL REFERENCES suppliers(id),
    waste_type_id INTEGER NOT NULL REFERENCES waste_types(id),
    cell_id INTEGER REFERENCES storage_cells(id),
    weight_kg NUMERIC(10, 2) NOT NULL,
    datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operator_id INTEGER NOT NULL REFERENCES users(id)
);

CREATE TABLE processing_stages (
    id SERIAL PRIMARY KEY,
    receipt_id INTEGER NOT NULL REFERENCES receipts(id) ON DELETE CASCADE,
    stage VARCHAR(20) NOT NULL CHECK (stage IN ('sorting', 'pressing', 'shipping')),
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'in_progress'
);

CREATE TABLE shipments (
    id SERIAL PRIMARY KEY,
    processed_batch_id INTEGER NOT NULL REFERENCES processing_stages(id),
    recipient VARCHAR(200) NOT NULL,
    weight_kg NUMERIC(10, 2) NOT NULL,
    transport_doc VARCHAR(255)
);

CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id INTEGER,
    old_value TEXT,
    new_value TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (login, password_hash, role) VALUES
    ('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin'),
    ('operator1', 'e10adc3949ba59abbe56e057f20f883e', 'operator'),
    ('operator2', 'e10adc3949ba59abbe56e057f20f883e', 'operator');

INSERT INTO waste_types (name, density_kg_per_m3, recyclable) VALUES
    ('Пластик ПЭТ', 1380.00, TRUE),
    ('Макулатура', 700.00, TRUE),
    ('Картон', 120.00, TRUE),
    ('Стекло', 2500.00, TRUE),
    ('Металл', 7800.00, TRUE);

INSERT INTO suppliers (name, type, phone, email) VALUES
    ('Иванов И.И.', 'F', '+7-900-111-1111', 'ivanov@mail.ru'),
    ('Петрова А.С.', 'F', '+7-900-222-2222', 'petrova@mail.ru'),
    ('ООО ЭкоСбор', 'L', '+7-495-333-3333', 'eco@mail.ru'),
    ('ИП Сидоров', 'L', '+7-900-444-4444', 'sidorov@mail.ru');

INSERT INTO storage_cells (code, max_capacity_kg, current_load_kg) VALUES
    ('A-001', 5000.00, 1250.00),
    ('A-002', 5000.00, 3200.00),
    ('B-001', 10000.00, 4500.00),
    ('B-002', 10000.00, 7800.00);

INSERT INTO receipts (supplier_id, waste_type_id, cell_id, weight_kg, datetime, operator_id) VALUES
    (1, 1, 1, 150.50, '2025-10-01 09:15:00', 2),
    (2, 2, 2, 85.00, '2025-10-02 10:30:00', 2),
    (3, 1, 1, 520.00, '2025-10-03 11:00:00', 3),
    (3, 3, 3, 310.00, '2025-10-05 14:00:00', 3),
    (4, 5, 4, 180.00, '2025-10-08 09:00:00', 2),
    (1, 4, 2, 450.00, '2025-10-10 15:00:00', 3),
    (2, 1, 1, 95.00, '2025-10-12 10:00:00', 2),
    (3, 2, 3, 200.00, '2025-10-15 08:30:00', 3);

INSERT INTO processing_stages (receipt_id, stage, start_time, end_time, status) VALUES
    (1, 'sorting', '2025-10-01 10:00:00', '2025-10-01 12:00:00', 'completed'),
    (1, 'pressing', '2025-10-01 13:00:00', '2025-10-01 14:30:00', 'completed'),
    (1, 'shipping', '2025-10-02 09:00:00', '2025-10-02 10:00:00', 'completed'),
    (2, 'sorting', '2025-10-02 11:00:00', '2025-10-02 12:30:00', 'completed'),
    (3, 'sorting', '2025-10-03 13:00:00', '2025-10-03 16:00:00', 'completed'),
    (3, 'pressing', '2025-10-04 09:00:00', '2025-10-04 12:00:00', 'completed'),
    (4, 'sorting', '2025-10-05 15:00:00', NULL, 'in_progress');

INSERT INTO shipments (processed_batch_id, recipient, weight_kg, transport_doc) VALUES
    (3, 'ООО Полимер', 145.00, 'ТН-2025-001');

INSERT INTO audit_log (user_id, action, entity_type, entity_id, new_value, timestamp) VALUES
    (2, 'CREATE', 'receipt', 1, '150.50 кг', '2025-10-01 09:15:00'),
    (2, 'CREATE', 'receipt', 2, '85.00 кг', '2025-10-02 10:30:00');

