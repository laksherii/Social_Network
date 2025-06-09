INSERT INTO users(email, first_name,last_name, birth_day, enabled, gender, role)
VALUES ('john.doe@example.com', 'first_name','last_name', '1992-04-20', true, 'MALE', 'ROLE_ADMIN');
INSERT INTO wall(user_id)
VALUES (1);