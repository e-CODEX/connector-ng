/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/**
 * Provides infrastructure-level security components for business document validation and secure
 * container creation.
 *
 * <p>This package contains the connector infrastructure services responsible for technical and
 * legal trust validation of business documents and their related security artefacts. It covers
 * validation of XML and PDF payloads, generation of Trust-OK tokens, certificate and trust-chain
 * checks, cryptographic support, and creation of ASiC-S containers used to package secured outbound
 * documents.
 *
 * <p>The components in this package operate at infrastructure level and support higher-level
 * connector workflows by producing and validating trust evidence required before a business
 * document is accepted, packaged, or transmitted.
 */
package eu.ecodex.connector.infrastructure.outbound.security;
