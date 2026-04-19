CREATE TABLE partner_api_key
(
    id         UUID PRIMARY KEY,
    partner_id VARCHAR(255) NOT NULL,
    hashed_key VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP   NOT NULL,
    active     BOOLEAN     NOT NULL DEFAULT TRUE
);

ALTER TABLE chat_session DROP CONSTRAINT chat_session_session_id_key;

ALTER TABLE chat_session
    ADD COLUMN session_type VARCHAR(20) NOT NULL DEFAULT 'WEB';

ALTER TABLE chat_session
    ADD CONSTRAINT uq_chat_session_partner_type
        UNIQUE (partner_id, session_id, session_type);
