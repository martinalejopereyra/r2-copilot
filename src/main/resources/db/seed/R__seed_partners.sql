INSERT INTO partner_onboarding (id, partner_id, current_status, context_json)
VALUES
    (gen_random_uuid(), 'ubereats',  'START',           '{"contact": "eng@ubereats.com",   "region": "US"}'),
    (gen_random_uuid(), 'rappi',     'AUTH_CONFIGURED', '{"contact": "platform@rappi.com", "region": "LATAM"}'),
    (gen_random_uuid(), 'doordash',  'WEBHOOK_SET',     '{"contact": "eng@doordash.com",   "region": "US"}'),
    (gen_random_uuid(), 'pedidosya', 'LIVE',            '{"contact": "tech@pedidosya.com", "region": "LATAM"}')
    ON CONFLICT (partner_id) DO NOTHING;