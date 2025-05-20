insert into users (email, first_name, last_name, birth_day, enabled, gender, role)
values ('sender@mail.com', 'Sender', 'User', '1990-01-01', true, 'MALE', 'ROLE_USER'),
       ('recipient@mail.com', 'Recipient', 'User', '1991-02-02', true, 'FEMALE', 'ROLE_USER'),
       ('third@mail.com', 'Recipient', 'User', '1991-02-02', true, 'FEMALE', 'ROLE_USER'),
       ('fourth@mail.com', 'Recipient', 'User', '1991-02-02', true, 'FEMALE', 'ROLE_USER');

insert into messages (id, sender_id, content, created_at)
values (100, 1, 'Hello from sender!', now());

insert into private_messages (message_id, recipient_id)
values (100, 2);
