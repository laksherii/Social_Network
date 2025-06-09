CREATE TABLE if not exists users
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

CREATE TABLE IF NOT EXISTS wall
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS friend_request
(
    id           BIGSERIAL PRIMARY KEY,
    sender_id    BIGINT       NOT NULL REFERENCES users (id),
    recipient_id BIGINT       NOT NULL REFERENCES users (id),
    status       VARCHAR(100) NOT NULL,
    UNIQUE (sender_id, recipient_id)
);

CREATE TABLE IF NOT EXISTS community
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    admin_id BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_community
(
    user_id      BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    community_id BIGINT NOT NULL REFERENCES community (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, community_id)
);

CREATE TABLE IF NOT EXISTS messages
(
    id           BIGSERIAL PRIMARY KEY,
    sender_id    BIGINT NOT NULL REFERENCES users (id),
    content      TEXT   NOT NULL,
    message_type VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS group_chat
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS group_chat_message
(
    message_id BIGINT NOT NULL REFERENCES messages (id),
    group_id   BIGINT NOT NULL REFERENCES group_chat (id)
);

CREATE TABLE IF NOT EXISTS community_messages
(
    message_id   BIGINT PRIMARY KEY REFERENCES messages (id) ON DELETE CASCADE,
    community_id BIGINT NOT NULL REFERENCES community (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS group_chat_user
(
    group_chat_id BIGINT NOT NULL REFERENCES group_chat (id),
    user_id       BIGINT NOT NULL REFERENCES users (id),
    PRIMARY KEY (group_chat_id, user_id)
);

CREATE TABLE IF NOT EXISTS user_friends
(
    user_id   BIGINT NOT NULL REFERENCES users (id),
    friend_id BIGINT NOT NULL REFERENCES users (id),
    PRIMARY KEY (user_id, friend_id),
    CHECK (user_id != friend_id)
);

CREATE TABLE IF NOT EXISTS wall_messages
(
    id         BIGSERIAL PRIMARY KEY,
    wall_id    BIGINT NOT NULL REFERENCES wall (id),
    message_id BIGINT NOT NULL REFERENCES messages (id),
    UNIQUE (wall_id, message_id)
);

CREATE TABLE IF NOT EXISTS private_messages
(
    message_id   BIGINT PRIMARY KEY REFERENCES messages (id) ON DELETE CASCADE,
    recipient_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE
);

INSERT INTO users (email, password, first_name, last_name, birth_day, enabled, gender, role)
VALUES
    ('elizabeth@example.com', '$2a$10$8xZjwgpW0lGR3aYtXWuSgebNr6bpb31XKFmeskvNg', 'Elizabeth', 'Johnson', '1992-08-20', TRUE, 'FEMALE', 'ROLE_ADMIN'),
    ('christopher@example.com', '$2a$10$6s4Q2i.8d5j2.zl7qFNy5udOWoMOavcvnzPySLLnT6kmA/2hkUnm6', 'Christopher', 'Williams', '1988-11-03', TRUE, 'MALE', 'ROLE_USER'),
    ('victoria@example.com', '$2a$10$MeUPEDJxuk14vhpJtkZQneRum7IIJmRBCCwgb.LTkmT8AmfjkdKYm', 'Victoria', 'Brown', '1995-02-28', TRUE, 'FEMALE', 'ROLE_MODERATOR'),
    ('benjamin@example.com', '$2a$10$1fdtPJRS/RnQK3PpMPimVuxog/tT5Ekc/zCo9muReYgP3cZa/j4ea', 'Benjamin', 'Jones', '1993-07-10', TRUE, 'MALE', 'ROLE_USER'),
    ('natalie@example.com', '$2a$10$NeMBlFBkVawmb5cKyLnmxut8eZvHOQsa0EWfSh4WcpiVjOml8FBGG', 'Natalie', 'Garcia', '1991-04-22', TRUE, 'FEMALE', 'ROLE_USER');

INSERT INTO messages (sender_id, content, message_type, created_at)
VALUES
    (1, 'Привет всем! Как дела?', 'PRIVATE', NOW() - INTERVAL '5 days'),
    (2, 'Кто пойдет на встречу в пятницу?', 'GROUP', NOW() - INTERVAL '4 days'),
    (3, 'Новости сообщества: скоро будет обновление!', 'COMMUNITY', NOW() - INTERVAL '3 days'),
    (4, 'Личное сообщение для тебя', 'PRIVATE', NOW() - INTERVAL '2 days'),
    (5, 'Обсудим новый проект?', 'GROUP', NOW() - INTERVAL '1 day'),
    (6, 'Важное объявление для всех участников', 'COMMUNITY', NOW()),
    (1, 'Как прошли выходные?', 'WALL', NOW()),
    (2, 'Поделюсь своими впечатлениями', 'WALL', NOW()),
    (3, 'Новая фотография на стене', 'WALL', NOW());

INSERT INTO private_messages (message_id, recipient_id)
VALUES
    (1, 2),
    (4, 1);

INSERT INTO group_chat (name)
VALUES
    ('General Group'),
    ('Work Group'),
    ('Friends');

INSERT INTO group_chat_message (message_id, group_id)
VALUES
    (2, 1),
    (5, 2);

INSERT INTO group_chat_user (group_chat_id, user_id)
VALUES
    (1, 1), (1, 2), (1, 3), (1, 4),
    (2, 1), (2, 3), (2, 5),
    (3, 2), (3, 4), (3, 6);

INSERT INTO community (name, admin_id)
VALUES
    ('Real Madrid', 1),
    ('Liverpool', 2),
    ('Manchester United', 4);

INSERT INTO user_community (user_id, community_id)
VALUES
    (1, 1), (1, 2),
    (2, 2), (2, 3),
    (3, 1), (3, 3),
    (4, 1), (4, 2),
    (5, 1), (4, 3),
    (6, 2), (6, 3);

INSERT INTO community_messages (message_id, community_id)
VALUES
    (3, 1),
    (6, 2);

INSERT INTO wall_messages (wall_id, message_id)
VALUES
    (1, 7),
    (2, 8),
    (3, 9);

INSERT INTO user_friends (user_id, friend_id)
VALUES
    (1,4),
    (2,5),
    (3,5),
    (5,1);

INSERT INTO friend_request (sender_id, recipient_id, status)
VALUES
    (1, 4, 'UNDEFINED'),
    (2, 5, 'ACCEPTED'),
    (3, 6, 'REJECTED'),
    (4, 1, 'UNDEFINED'),
    (5, 2, 'ACCEPTED'),
    (6, 3, 'DELETED');