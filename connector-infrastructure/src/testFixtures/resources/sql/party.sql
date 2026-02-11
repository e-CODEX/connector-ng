INSERT INTO CONNECTOR_PARTIES
(id, uuid, name, identifier, identifier_type, role, role_type, processing_mode_id, created_at,
 updated_at)
VALUES (1,
        '3d88bd98-215c-4a48-b10a-a51d2b5d13ba',
        'service_blue_ecodex',
        'BL',
        'urn:oasis:names:tc:ebcore:partyid-type:ecodex',
        'GW',
        'INITIATOR',
        1
       ),
    (2,
     'afeb93ac-535d-464a-9b68-8b77ee44a960',
     'service_red_ecodex',
     'RE',
     'urn:oasis:names:tc:ebcore:partyid-type:ecodex',
     'GW',
     'RESPONDER',
     1,
     now(),
     now()
    );
