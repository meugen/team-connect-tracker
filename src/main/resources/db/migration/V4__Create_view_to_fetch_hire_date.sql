CREATE VIEW user_hire_date AS SELECT user_id, min(start_date) hire_date
                              FROM user_position GROUP BY user_id;