--liquibase formatted sql

--changeset davimf:1.0.0
--comment: creation of tables: user, user_authorizations, authorization_tokens and order
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "users" (
    "id" UUID PRIMARY KEY,
    "role" VARCHAR(255) NOT NULL,
    "user_name" VARCHAR(255) not null,
    "created_at" TIMESTAMP default NOW() NOT NULL,
    "updated_at" TIMESTAMP default NOW() NOT NULL);

CREATE INDEX idx_users_user_name ON users (user_name);

CREATE TABLE "user_authorizations" (
    "user_id" UUID PRIMARY KEY references "users"("id"),
    "password" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP default NOW() NOT NULL,
    "updated_at" TIMESTAMP default NOW() NOT NULL);

CREATE TABLE "authorization_tokens" (
    "id" UUID PRIMARY KEY,
    "user_id" UUID REFERENCES "user_authorizations"("user_id"),
    "type" VARCHAR(255) NOT NULL,
    "token" TEXT NOT NULL UNIQUE,
    "revoked" BOOLEAN NOT NULL DEFAULT FALSE,
    "expired" BOOLEAN NOT NULL DEFAULT FALSE,
    "created_at" TIMESTAMP DEFAULT NOW() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT NOW() NOT NULL);

CREATE INDEX idx_authorization_tokens_token ON authorization_tokens (token);

create table "orders" (
    "id" UUID PRIMARY KEY,
    "status" VARCHAR(255) NOT NULL,
    "user_id" UUID references "users"("id"),
    "origin_address" VARCHAR(255) NOT NULL,
    "delivery_address" VARCHAR(255) NOT NULL,
    "package_amount" smallint NOT NULL,
    "created_at" TIMESTAMP default NOW() NOT NULL,
    "updated_at" TIMESTAMP default NOW() NOT NULL);
