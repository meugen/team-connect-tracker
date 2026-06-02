CREATE TABLE holidays (
    id VARCHAR(200) NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    date DATE NOT NULL
);

CREATE INDEX idx_holidays_date ON holidays (date);
