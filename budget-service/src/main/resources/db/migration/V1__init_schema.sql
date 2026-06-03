CREATE TABLE account (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL UNIQUE,
    balance    NUMERIC(19,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE transaction (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id       UUID          NOT NULL REFERENCES account(id),
    amount           NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    type             VARCHAR(20)   NOT NULL,
    category         VARCHAR(100)  NOT NULL,
    description      VARCHAR(500),
    transaction_date TIMESTAMP     NOT NULL,
    created_at       TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE category_budget (
    id            UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id    UUID          NOT NULL REFERENCES account(id),
    category      VARCHAR(100)  NOT NULL,
    monthly_limit NUMERIC(19,2) NOT NULL CHECK (monthly_limit > 0),
    created_at    TIMESTAMP     NOT NULL DEFAULT now(),
    UNIQUE (account_id, category)
);

CREATE INDEX idx_transaction_account_id ON transaction(account_id);
CREATE INDEX idx_transaction_category ON transaction(category);
CREATE INDEX idx_transaction_date ON transaction(transaction_date);
CREATE INDEX idx_category_budget_account_id ON category_budget(account_id);
