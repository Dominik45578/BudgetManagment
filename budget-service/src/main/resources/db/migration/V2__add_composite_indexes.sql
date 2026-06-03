
CREATE INDEX idx_transaction_account_date ON transaction(account_id, transaction_date);

CREATE INDEX idx_transaction_category_lower ON transaction(lower(category));

CREATE INDEX idx_category_budget_account_category ON category_budget(account_id, lower(category));
