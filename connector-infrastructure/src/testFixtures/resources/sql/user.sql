INSERT INTO connector_roles (id, uuid, name, created_at, updated_at)
VALUES (2, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_fake_role_test', 'ROLE_TEST', now(), now()),
       (3, '2c7f58331-be5f-463a-8b17-1bfe0eab7bd4_fake_role_user', 'ROLE_USER', now(), now()),
       (4, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04d_fake_role_admin', 'ROLE_ADMIN_IT', now(), now()),
       (1, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_default_role_admin','ROLE_ADMIN', now(),now())
            ON DUPLICATE KEY UPDATE
                 name = VALUES(name),
                 uuid = VALUES(uuid);

INSERT INTO connector_users (id, uuid, username, password, email, enabled, created_at, updated_at)
VALUES (2, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_fake_user_admin', 'test-admin-it','$2a$12$pwdAdminIt','admin-it@email.com', true, now(), now()),
       (3, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_fake_user_test', 'test-user-it','$2a$12$Z4jT8Cvg/CcmNxJ3aNXeleQ/upt3LIla4e2mghCvUyvhj9P3BpSjS','user-it@email.com', true, now(), now()),
       (4, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_fake_user_test2', 'test-user2-it','$2a$12$pwdUser2It','user2-it@email.com', true, now(), now()),
       (1, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_default_admin', 'admin', '$2a$12$5jl8Wz2LwcMLc7.yarZT8eOnWtu4tz1IvScxxSPZyGTtrKBTdTt3W',  'admin@example.org',true,now(), now())
            ON DUPLICATE KEY UPDATE
                uuid = VALUES(uuid),
                username = VALUES(username),
                password = VALUES(password),
                email = VALUES(email),
                enabled = VALUES(enabled);

INSERT IGNORE INTO connector_users_roles(user_id, role_id) VALUES (1, 1);
INSERT IGNORE INTO connector_users_roles(user_id, role_id) VALUES (2, 1);
INSERT IGNORE INTO connector_users_roles(user_id, role_id) VALUES (2, 3);
INSERT IGNORE INTO connector_users_roles(user_id, role_id) VALUES (3, 2);
INSERT IGNORE INTO connector_users_roles(user_id, role_id) VALUES (3, 3);