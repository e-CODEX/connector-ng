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
 * Outbound DSS adapters of the connector: the implementations through which the core performs
 * cryptographic operations using the Digital Signature Services (DSS) library.
 *
 * <p>Classes in this package implement the signature-related outbound ports of
 * {@link eu.ecodex.connector.application.port.spi}, such as signing content, validating signatures,
 * handling certificates and trust anchors, and checking certificate validity against trusted lists.
 * They translate between domain types and DSS types, so that no DSS or cryptographic library
 * details reach the core.
 *
 * <p>DSS adapters contain no business rules.
 */
package eu.ecodex.connector.infrastructure.adapter.out.dss;
