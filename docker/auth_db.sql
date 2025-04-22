CREATE TABLE IF NOT EXISTS users
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

CREATE TABLE IF NOT EXISTS oauth2_registered_client
(
    id                            VARCHAR(100) PRIMARY KEY,
    client_id                     VARCHAR(100) NOT NULL,
    client_id_issued_at           TIMESTAMP,
    client_secret                 VARCHAR(200),
    client_secret_expires_at      TIMESTAMP,
    client_name                   VARCHAR(200),
    client_authentication_methods TEXT NOT NULL,
    authorization_grant_types     TEXT NOT NULL,
    redirect_uris                 TEXT NOT NULL,
    post_logout_redirect_uris     TEXT,
    scopes                        TEXT NOT NULL,
    client_settings               TEXT NOT NULL,
    token_settings                TEXT NOT NULL
);
