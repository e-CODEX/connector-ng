/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

INSERT INTO connector_processing_modes (
    id,
    uuid,
    description,
    content,
    filename,
    business_domain_id,
    created_at,
    updated_at
)
VALUES (
           1,
           '4f10aed9-2e5f-4780-87f7-5fe1070d5ccf',
           'test processing mode',
           '<?xml version="1.0" encoding="UTF-8"?>',
           'pmode.xml',
           (SELECT id FROM connector_business_domains WHERE identifier = 'default_business_domain'),
           now(),
           now()
       );
