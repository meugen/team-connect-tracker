
CREATE TABLE user_project_task (
    user_id INTEGER NOT NULL,
    project_id INTEGER NOT NULL,
    task_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    PRIMARY KEY (user_id, project_id, task_id)
);
