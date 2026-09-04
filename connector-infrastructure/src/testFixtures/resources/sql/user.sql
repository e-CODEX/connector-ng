INSERT INTO connector_roles (id, uuid, name, created_at, updated_at)
VALUES (2, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_fake_role_test', 'ROLE_TEST', now(), now()),
       (3, '2c7f58331-be5f-463a-8b17-1bfe0eab7bd4_fake_role_user', 'ROLE_USER', now(), now()),
       (4, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04d_fake_role_admin', 'ROLE_ADMIN_IT', now(), now()),
       (1, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_default_role_admin', 'ROLE_ADMIN', now(), now())
ON DUPLICATE KEY UPDATE name = VALUES(name),
                        uuid = VALUES(uuid);

INSERT INTO connector_users (id, uuid, username, password, email, enabled, created_at, updated_at)
VALUES (2, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_fake_user_admin', 'test-admin-it', '$2a$12$pwdAdminIt', 'admin-it@email.com', true, now(), now()),
       (3, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_fake_user_test', 'test-user-it','$2a$12$Z4jT8Cvg/CcmNxJ3aNXeleQ/upt3LIla4e2mghCvUyvhj9P3BpSjS', 'user-it@email.com', true,now(), now()),
       (4, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_fake_user_test2', 'test-user2-it','$2a$12$pwdUser2It', 'user2-it@email.com', true, now(), now()),
       (1, 'd43bfa931-3c25-47e4-b377-bf4ce7b0d04c_default_admin', 'admin', '$2a$12$5jl8Wz2LwcMLc7.yarZT8eOnWtu4tz1IvScxxSPZyGTtrKBTdTt3W', 'admin@example.org', true,now(), now())
ON DUPLICATE KEY UPDATE uuid     = VALUES(uuid),
                        username = VALUES(username),
                        password = VALUES(password),
                        email    = VALUES(email),
                        enabled  = VALUES(enabled);

INSERT IGNORE INTO connector_users_roles(user_id, role_id)
VALUES (1, 1),
       (2, 1),
       (2, 3),
       (3, 2),
       (3, 3);

INSERT IGNORE INTO connector_refresh_tokens(id, token, user_id, expires_at, revoked, created_at, updated_at)
VALUES(1,'4759bc7f-eba7-4009-8987-a368a3389896', 1, '2026-08-01 15:37:03.160645', true, now(), now()),
      (2,'ed7473ec-49e8-46de-b7c7-79eedc460273', 1, '2026-08-03 15:37:03.160645', true, now(), now()),
      (3,'410a8371-eed9-42b6-a1e1-30980b804bfa', 1, '2026-08-04 15:37:03.160645', true, now(), now()),
      (4,'671ffa19-c75c-4cf3-bf63-62dad240a7b9', 1, '2026-08-18 10:30:03.160645', false, now(), now()),
      (5,'7817701a-724a-4715-8793-28339e37fccb', 3, '2026-10-01 15:37:03.160645', true, now(), now()),
      (6,'291b571a-1511-45a0-b990-368b5139011d', 3, '2026-10-10 15:37:03.160645', false, now(), now());
