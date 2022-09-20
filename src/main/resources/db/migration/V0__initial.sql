CREATE SCHEMA IF NOT EXISTS notcord;

CREATE TABLE IF NOT EXISTS notcord.guilds (
    id VARCHAR(24) PRIMARY KEY,
    owner_id VARCHAR(24) NOT NULL,
    name VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS notcord.channels (
    id VARCHAR(24) PRIMARY KEY,
    type INTEGER NOT NULL,
    name VARCHAR(40) NOT NULL,
    guild_id VARCHAR(70)
);

CREATE TABLE IF NOT EXISTS notcord.users (
    id VARCHAR(24) PRIMARY KEY,
    username VARCHAR(40) NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS notcord.oauth2_credentials (
    client_id VARCHAR(24) PRIMARY KEY,
    client_secret VARCHAR(32) NOT NULL,
    app_name VARCHAR(40) NOT NULL,
    redirect_uri TEXT NOT NULL,
    owner_id VARCHAR(70) NOT NULL
);

CREATE TABLE IF NOT EXISTS notcord.oauth2_tokens (
    id VARCHAR(32) PRIMARY KEY,
    type VARCHAR(12) NOT NULL,
    access_token TEXT NOT NULL UNIQUE,
    refresh_token TEXT DEFAULT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_in BIGINT NOT NULL,
    user_id VARCHAR(24) NOT NULL,
    scope INTEGER NOT NULL
);
