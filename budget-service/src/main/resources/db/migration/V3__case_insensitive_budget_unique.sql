-- Drop the case-sensitive UNIQUE constraint from V1
ALTER TABLE category_budget DROP CONSTRAINT IF EXISTS category_budget_account_id_category_key;

-- Drop the non-unique index from V2 if it exists
DROP INDEX IF EXISTS idx_category_budget_account_category;

-- Create a case-insensitive UNIQUE composite index
CREATE UNIQUE INDEX idx_category_budget_account_category_uniq ON category_budget(account_id, lower(category));
