
CREATE TABLE time_logs (
    id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    project_id INTEGER NOT NULL,
    task_id INTEGER NOT NULL,
    date DATE NOT NULL,
    duration_minutes INTEGER NOT NULL,
    description TEXT,
    media_file_id INTEGER,
    CONSTRAINT fk_user_project_task FOREIGN KEY (user_id, project_id, task_id)
        REFERENCES user_project_task (user_id, project_id, task_id),
    CONSTRAINT fk_media_file FOREIGN KEY (media_file_id)
        REFERENCES media_files (id),
    PRIMARY KEY (id)
);

CREATE SEQUENCE time_logs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY time_logs ALTER COLUMN id SET DEFAULT nextval('time_logs_id_seq'::regclass);
