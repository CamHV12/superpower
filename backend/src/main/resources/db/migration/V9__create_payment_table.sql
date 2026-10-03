CREATE TABLE payments (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    payment_date DATE NOT NULL,
    method VARCHAR(30) NOT NULL,
    reference_number VARCHAR(100),
    notes VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_payments_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoices(id),

    CONSTRAINT chk_payments_amount
        CHECK (amount > 0),

    CONSTRAINT chk_payments_method
        CHECK (method IN ('CASH', 'BANK_TRANSFER', 'CREDIT_CARD', 'OTHER'))
);

CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);
