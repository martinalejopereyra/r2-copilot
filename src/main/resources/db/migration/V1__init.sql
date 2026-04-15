CREATE TABLE partner_onboarding
(
    id             UUID PRIMARY KEY,
    partner_id     VARCHAR(255) NOT NULL UNIQUE,
    current_status VARCHAR(50)  NOT NULL DEFAULT 'START',
    context_json   JSONB,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE chat_session
(
    id         UUID PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    partner_id VARCHAR(255) NOT NULL,
    started_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    ended_at   TIMESTAMP,
    CONSTRAINT fk_chat_session_partner
        FOREIGN KEY (partner_id)
            REFERENCES partner_onboarding (partner_id)
);

CREATE TABLE chat_message
(
    id         UUID PRIMARY KEY,
    session_id UUID        NOT NULL,
    role       VARCHAR(20) NOT NULL,
    content    TEXT        NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_chat_message_session
        FOREIGN KEY (session_id)
            REFERENCES chat_session (id)
);

-- indexes that matter for your query patterns
CREATE INDEX idx_chat_session_partner_id ON chat_session (partner_id);
CREATE INDEX idx_chat_session_session_id ON chat_session (session_id);
CREATE INDEX idx_chat_message_session_id ON chat_message (session_id);
CREATE INDEX idx_partner_onboarding_partner_id ON partner_onboarding (partner_id);

-- auto-update updated_at on partner_onboarding
CREATE
OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at
= NOW();
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_partner_onboarding_updated_at
    BEFORE UPDATE
    ON partner_onboarding
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();