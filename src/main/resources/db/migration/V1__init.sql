-- V1: initial schema for TaskBoard
-- Flyway runs this once and records it in flyway_schema_history.

CREATE TABLE IF NOT EXISTS users (
    id              UUID PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    display_name    VARCHAR(120) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS projects (
    id              UUID PRIMARY KEY,
    owner_id        UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name            VARCHAR(160) NOT NULL,
    description     TEXT NOT NULL DEFAULT '',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_projects_owner_id ON projects (owner_id);

-- Demo user for CRUD until auth is added.
INSERT INTO users (id, email, password_hash, display_name)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'demo@taskboard.local',
    'not-a-real-hash-yet',
    'Demo User'
)
ON CONFLICT (email) DO NOTHING;
