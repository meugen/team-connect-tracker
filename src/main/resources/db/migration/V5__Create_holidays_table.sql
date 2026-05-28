CREATE TABLE holidays (
    id VARCHAR(200) NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    date DATE NOT NULL
);

CREATE INDEX idx_holidays_date ON holidays (date);

CREATE TABLE holidays_updates (
    id INTEGER NOT NULL,
    year INT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE holidays_updates_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY holidays_updates ALTER COLUMN id SET DEFAULT nextval('holidays_updates_id_seq'::regclass);

ALTER TABLE ONLY holidays_updates
    ADD CONSTRAINT holidays_updates_pkey PRIMARY KEY (id);

CREATE INDEX idx_holidays_updates_year ON holidays_updates (year);

CREATE TABLE holiday_options (
    id INTEGER NOT NULL,
    holiday_id VARCHAR(200) NOT NULL,
    is_holiday BOOLEAN NOT NULL
);

CREATE SEQUENCE holiday_options_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY holiday_options ALTER COLUMN id SET DEFAULT nextval('holiday_options_id_seq'::regclass);

ALTER TABLE ONLY holiday_options
    ADD CONSTRAINT holiday_options_pkey PRIMARY KEY (id);

CREATE UNIQUE INDEX idx_holiday_options_holiday_id ON holiday_options (holiday_id);
