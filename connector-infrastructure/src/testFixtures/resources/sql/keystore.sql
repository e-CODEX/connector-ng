INSERT INTO connector_keystores
(id, uuid, description, content, password, type, filename, processing_mode_id, created_at,
 updated_at)
VALUES (1,
        'f81647fc-d870-4275-bdbd-982f32e5235f',
        'test processing mode keystore',
        X'0102030A0FFF',
        '12345',
        'JKS',
        'keystore.jks',
        1,
        now(),
        now()
       );
