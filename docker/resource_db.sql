CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    birth_day  DATE         NOT NULL,
    enabled    BOOLEAN      NOT NULL,
    gender     VARCHAR(100) NOT NULL,
    role       VARCHAR(100) NOT NULL
);

CREATE TABLE wall
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users (id)
);

CREATE TABLE friend_request
(
    id           BIGSERIAL PRIMARY KEY,
    sender_id    BIGINT       NOT NULL REFERENCES users (id),
    recipient_id BIGINT       NOT NULL REFERENCES users (id),
    status       VARCHAR(100) NOT NULL
);

CREATE TABLE community
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    admin_id BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE user_community
(
    user_id      BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    community_id BIGINT NOT NULL REFERENCES community (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, community_id)
);

CREATE TABLE messages
(
    id           BIGSERIAL PRIMARY KEY,
    sender_id    BIGINT NOT NULL REFERENCES users (id),
    content      TEXT   NOT NULL,
    message_type VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE group_chat
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE group_chat_message
(
    message_id BIGINT NOT NULL REFERENCES messages (id),
    group_id   BIGINT NOT NULL REFERENCES group_chat (id)
);

CREATE TABLE community_messages
(
    message_id   BIGINT PRIMARY KEY REFERENCES messages (id) ON DELETE CASCADE,
    community_id BIGINT NOT NULL REFERENCES community (id) ON DELETE CASCADE
);

CREATE TABLE group_chat_user
(
    group_chat_id BIGINT NOT NULL REFERENCES group_chat (id),
    user_id       BIGINT NOT NULL REFERENCES users (id),
    PRIMARY KEY (group_chat_id, user_id)
);

CREATE TABLE user_friends
(
    user_id   BIGINT NOT NULL REFERENCES users (id),
    friend_id BIGINT NOT NULL REFERENCES users (id),
    PRIMARY KEY (user_id, friend_id),
    CHECK (user_id != friend_id)
);

CREATE TABLE wall_messages
(
    id         BIGSERIAL PRIMARY KEY,
    wall_id    BIGINT NOT NULL REFERENCES wall (id),
    message_id BIGINT NOT NULL REFERENCES messages (id),
    UNIQUE (wall_id, message_id)
);

CREATE TABLE private_messages
(
    message_id   BIGINT PRIMARY KEY REFERENCES messages (id) ON DELETE CASCADE,
    recipient_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    is_read      BOOLEAN   DEFAULT FALSE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



INSERT INTO users (email, password, first_name, last_name, birth_day, enabled, gender, role)
VALUES ('ivan.petrov@example.com', '$2a$10$xJwL5v5Jz5U6ZQ5F5Q5Z.e', 'Иван', 'Петров', '1990-05-15', true, 'MALE',
        'ROLE_USER'),
       ('anna.sidorova@example.com', '$2a$10$yJwL5v5Jz5U6ZQ5F5Q5Z.e', 'Анна', 'Сидорова', '1985-08-22', true, 'FEMALE',
        'ROLE_USER'),
       ('alex.smirnov@example.com', '$2a$10$zJwL5v5Jz5U6ZQ5F5Q5Z.e', 'Алексей', 'Смирнов', '1978-11-30', true, 'MALE',
        'ROLE_ADMIN'),
       ('elena.ivanova@example.com', '$2a$10$aJwL5v5Jz5U6ZQ5F5Q5Z.e', 'Елена', 'Иванова', '1995-03-10', false, 'FEMALE',
        'ROLE_USER'),
       ('dmitry.kuznetsov@example.com', '$2a$10$bJwL5v5Jz5U6ZQ5F5Q5Z.e', 'Дмитрий', 'Кузнецов', '1982-07-19', true,
        'MALE', 'ROLE_MODERATOR');
