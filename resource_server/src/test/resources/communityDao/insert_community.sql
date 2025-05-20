insert into users (email, first_name, last_name, birth_day, enabled, gender, role)
values ('sender@mail.com', 'Sender', 'User', '1990-01-01', true, 'MALE', 'ROLE_USER'),
       ('recipient@mail.com', 'Recipient', 'User', '1991-02-02', true, 'FEMALE', 'ROLE_USER'),
       ('third@mail.com', 'Recipient', 'User', '1991-02-02', true, 'FEMALE', 'ROLE_USER'),
       ('fourth@mail.com', 'Recipient', 'User', '1991-02-02', true, 'FEMALE', 'ROLE_USER');

INSERT INTO community (id, name, description, admin_id)
VALUES (100, 'Community 1', 'Description 1', 1),
       (101, 'Community 2', 'Description 2', 2),
       (102, 'Community 3', 'Description 3', 1);