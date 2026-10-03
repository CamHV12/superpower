CREATE TABLE customers (
    id UUID PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(30),
    tax_code VARCHAR(30) UNIQUE,
    contact_person VARCHAR(150),
    address VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_customers_type CHECK (type IN ('INDIVIDUAL', 'COMPANY'))
);

CREATE INDEX idx_customers_name ON customers(name);
CREATE INDEX idx_customers_active ON customers(active);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_tax_code ON customers(tax_code);
