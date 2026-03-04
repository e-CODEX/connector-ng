INSERT INTO connector_processing_modes
(id, uuid, description, content, filename, business_domain_id, truststore_id, created_at,
 updated_at)
VALUES (1,
        '4f10aed9-2e5f-4780-87f7-5fe1070d5ccf',
        'test processing mode',
        '<?xml version="1.0" encoding="UTF-8"?>',
        'pmode.xml',
        1,
        null,
        now(),
        now()
       );
