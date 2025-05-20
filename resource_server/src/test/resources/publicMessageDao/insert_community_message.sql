INSERT INTO users (first_name, last_name, email, birth_day, gender, role, enabled)
VALUES ('Community', 'Admin', 'community.admin@example.com', '1990-01-01', 'MALE', 'ROLE_USER', true);
INSERT INTO community (name, description, admin_id)
VALUES ( 'Test Community', 'Community for testing purposes', 1);
