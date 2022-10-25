CREATE SCHEMA IF NOT EXISTS notcord;

-- Users
CREATE TABLE IF NOT EXISTS notcord.users
(
    id                 VARCHAR(24) PRIMARY KEY,
    username           VARCHAR(40) NOT NULL UNIQUE,
    email              TEXT        NOT NULL UNIQUE,
    password           TEXT        NOT NULL,
    default_avatar_svg TEXT        NOT NULL,
    email_verified     BOOLEAN     NOT NULL DEFAULT FALSE
);

-- Guilds
CREATE TABLE IF NOT EXISTS notcord.guilds
(
    id       VARCHAR(24) PRIMARY KEY,
    owner_id VARCHAR(24) NOT NULL REFERENCES notcord.users (id) ON DELETE CASCADE,
    name     VARCHAR(40) NOT NULL
);

-- Channels
CREATE TABLE IF NOT EXISTS notcord.channels
(
    id           VARCHAR(24) PRIMARY KEY,
    type         INTEGER NOT NULL,
    name         VARCHAR(40) DEFAULT NULL,
    guild_id     VARCHAR(24) DEFAULT NULL REFERENCES notcord.guilds (id) ON DELETE CASCADE,
    member_limit INTEGER     DEFAULT NULL,
    owner_id     VARCHAR(24) DEFAULT NULL REFERENCES notcord.users (id) ON DELETE CASCADE
);

-- DM Channel Members
CREATE TABLE IF NOT EXISTS notcord.dm_channel_members
(
    row_id       VARCHAR(24) PRIMARY KEY,
    channel_type INTEGER     NOT NULL,
    channel_id   VARCHAR(24) NOT NULL REFERENCES notcord.channels (id) ON DELETE CASCADE,
    user_id      VARCHAR(24) NOT NULL REFERENCES notcord.users (id) ON DELETE CASCADE
);

-- Messages
CREATE TABLE IF NOT EXISTS notcord.messages
(
    id         VARCHAR(24) PRIMARY KEY,
    channel_id VARCHAR(24) NOT NULL REFERENCES notcord.channels (id) ON DELETE CASCADE,
    author_id  VARCHAR(24) NOT NULL,
    guild_id   VARCHAR(24) DEFAULT NULL REFERENCES notcord.guilds (id) ON DELETE CASCADE,
    content    TEXT        NOT NULL
);

-- Oauth2
CREATE TABLE IF NOT EXISTS notcord.oauth2_credentials
(
    client_id        VARCHAR(24) PRIMARY KEY,
    client_secret    VARCHAR(32) NOT NULL,
    app_name         VARCHAR(40) NOT NULL,
    redirect_uri     TEXT        NOT NULL,
    owner_id         VARCHAR(70) NOT NULL REFERENCES notcord.users (id) ON DELETE CASCADE,
    default_icon_svg TEXT        NOT NULL
);

CREATE TABLE IF NOT EXISTS notcord.oauth2_tokens
(
    id            VARCHAR(32) PRIMARY KEY,
    type          VARCHAR(12)              NOT NULL,
    access_token  TEXT                     NOT NULL UNIQUE,
    refresh_token TEXT        DEFAULT NULL UNIQUE,
    expires_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_in    BIGINT                   NOT NULL,
    user_id       VARCHAR(24)              NOT NULL REFERENCES notcord.users (id) ON DELETE CASCADE,
    scope         INTEGER                  NOT NULL,
    client_id     VARCHAR(24) DEFAULT NULL REFERENCES notcord.oauth2_credentials (client_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notcord.oauth2_authorization_codes
(
    code       VARCHAR(32) PRIMARY KEY,
    user_id    VARCHAR(24)              NOT NULL REFERENCES notcord.users (id) ON DELETE CASCADE,
    client_id  VARCHAR(24)              NOT NULL REFERENCES notcord.oauth2_credentials (client_id) ON DELETE CASCADE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    scope      INTEGER                  NOT NULL
);
