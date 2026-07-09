
CREATE SEQUENCE tasks_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE tasks (
    id INTEGER NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL
);

ALTER TABLE ONLY tasks ALTER COLUMN id SET DEFAULT nextval('public.tasks_id_seq'::regclass);

ALTER TABLE ONLY tasks
    ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);
