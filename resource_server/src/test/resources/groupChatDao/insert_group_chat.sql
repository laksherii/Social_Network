insert into users (email, first_name, last_name, birth_day, enabled, gender, role)
values ('john@example.com', 'John', 'Doe', '1990-01-01', true, 'MALE', 'ROLE_USER'),
       ('jane@example.com', 'Jane', 'Smith', '1991-01-01', true, 'FEMALE', 'ROLE_USER');

insert into group_chat (id, name)
values (10, 'Test Chat');

insert into group_chat_user (group_chat_id, user_id)
values (10, 1),
       (10, 2);