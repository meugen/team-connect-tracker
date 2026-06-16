ALTER TABLE holidays ADD COLUMN day_off BOOLEAN NULL;

UPDATE holidays SET day_off = TRUE WHERE day_off IS NULL;

ALTER TABLE holidays ALTER COLUMN day_off SET NOT NULL;

