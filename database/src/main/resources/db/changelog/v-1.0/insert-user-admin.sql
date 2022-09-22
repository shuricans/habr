INSERT INTO users (username, password, first_name, condition, created, updated)
VALUES ('admin',
        '$2a$12$hqfFS9uY9PZiukO0U84fUeGZQithhej6WmdztEKfm1H7MrldmoInK',
        'Neo',
        'ACTIVE',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

INSERT INTO users_roles (user_id, role_id)
SELECT (SELECT user_id FROM users WHERE username = 'admin'),
       (SELECT role_id FROM roles WHERE name = 'ROLE_ADMIN');