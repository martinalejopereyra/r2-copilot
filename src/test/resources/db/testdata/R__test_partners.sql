DELETE
FROM chat_message;
DELETE
FROM chat_session;
DELETE
FROM partner_onboarding;

INSERT INTO partner_onboarding (id, partner_id, current_status)
VALUES (gen_random_uuid(), 'ubereats', 'START'),
       (gen_random_uuid(), 'rappi', 'AUTH_CONFIGURED'),
       (gen_random_uuid(), 'doordash', 'WEBHOOK_SET'),
       (gen_random_uuid(), 'pedidosya', 'LIVE')
ON CONFLICT (partner_id) DO UPDATE
    SET current_status = EXCLUDED.current_status;