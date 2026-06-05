ALTER TABLE category_budget DROP CONSTRAINT IF EXISTS category_budget_account_id_category_key;

DROP INDEX IF EXISTS idx_category_budget_account_category;

CREATE UNIQUE INDEX idx_category_budget_account_category_uniq ON category_budget(account_id, lower(category));
