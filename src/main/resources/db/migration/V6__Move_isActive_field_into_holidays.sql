DROP TABLE holiday_options;

DROP SEQUENCE holiday_options_id_seq;

ALTER TABLE holidays ADD COLUMN is_active BOOLEAN NULL;

UPDATE holidays
SET is_active = true;

ALTER TABLE holidays ALTER COLUMN is_active SET NOT NULL;
