/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

INSERT INTO connector_business_domains (id, uuid, identifier, description, enabled, source, created_at,
                             updated_at)
VALUES (1,
        'b25872bd-6056-4679-bbe4-3e8357bdb1b0',
        'default_business_domain',
        'default business domain',
        true,
        'IMPLEMENTATION',
        now(),
        now()
       );
