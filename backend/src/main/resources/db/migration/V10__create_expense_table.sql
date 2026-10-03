CREATE TABLE expenses (
    id UUID PRIMARY KEY,
    category VARCHAR(100) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    expense_date DATE NOT NULL,
    vendor VARCHAR(200),
    payment_method VARCHAR(30) NOT NULL,
    notes VARCHAR(1000),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_expenses_amount CHECK (amount > 0),
    CONSTRAINT chk_expenses_payment_method CHECK (payment_method IN ('CASH', 'BANK_TRANSFER', 'CREDIT_CARD', 'OTHER')),
    CONSTRAINT chk_expenses_status CHECK (status IN ('RECORDED', 'CANCELLED'))
);

CREATE INDEX idx_expenses_expense_date ON expenses(expense_date);
CREATE INDEX idx_expenses_category ON expenses(category);
CREATE INDEX idx_expenses_status ON expenses(status);
