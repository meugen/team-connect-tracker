
-- users:
-- 11 john.doe@example.com
-- 8 laura.wilson@example.com
-- 57 emma.wilson@example.com
-- 6 sarah.brown@example.com

-- projects:
-- 21 Internal
-- 14 InnovateX

-- valid user_id/project_id pairs
-- (11, 21)
-- (11, 14)
-- (6, 21)
-- (8, 21)
-- (57, 21)

INSERT INTO tasks (name, description) VALUES ('Sample', 'This is a sample task for testing purposes.');

INSERT INTO user_project_task (user_id, project_id, task_id)
    VALUES (11, 21, 1),
           (11, 14, 1),
           (6, 21, 1),
           (8, 21, 1),
           (57, 21, 1);

INSERT INTO time_logs (user_id, project_id, task_id, date, duration_minutes, description)
    VALUES (11, 21, 1, '2026-07-01', 120, 'Worked on the Internal project.'),
           (11, 14, 1, '2026-07-02', 350, 'Worked on the InnovateX project.'),
           (6, 21, 1, '2026-07-03', 180, 'Worked on the Internal project.'),
           (8, 21, 1, '2026-07-04', 200, 'Worked on the Internal project.'),
           (57, 21, 1, '2026-07-05', 150, 'Worked on the Internal project.');
