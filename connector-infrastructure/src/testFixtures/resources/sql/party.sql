INSERT INTO connector_parties (id, identifier, identifier_type, is_home, name, role, role_type, uuid, processing_mode_id, created_at, updated_at)
VALUES  (1, 'BL', 'urn:oasis:names:tc:ebcore:partyid-type:ecodex', true, 'service_blue_ecodex', 'GW', 'RESPONDER', '46e505d0-4703-48fb-8d09-3b7600b15caa', 1, now(), now()),
        (2, 'RE', 'urn:oasis:names:tc:ebcore:partyid-type:ecodex', false, 'service_red_ecodex', 'GW', 'INITIATOR', 'a719d231-1286-4e9c-a198-e8dd5fa269c4', 1, now(), now()),
        (3, 'BL', 'urn:oasis:names:tc:ebcore:partyid-type:ecodex', true, 'service_blue_ecodex', 'GW', 'INITIATOR', 'bc63341a-0667-47d7-a1e2-46a802663f06', 1, now(), now()),
        (4, 'RE', 'urn:oasis:names:tc:ebcore:partyid-type:ecodex', false, 'service_red_ecodex', 'GW', 'RESPONDER', '0b9d13eb-6b58-40ff-8feb-b881fc615e3c', 1, now(), now());
