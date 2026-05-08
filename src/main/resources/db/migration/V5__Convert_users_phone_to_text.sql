ALTER TABLE users ADD COLUMN phone_text text;

UPDATE users
SET phone_text = phone::text;

ALTER TABLE users
    DROP COLUMN phone;

ALTER TABLE users
    RENAME COLUMN phone_text TO phone;

UPDATE users SET phone = '{}' WHERE phone IS NULL;

ALTER TABLE users
    ALTER COLUMN phone SET NOT NULL;
