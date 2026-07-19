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
 * Provides domain model types for connector security validation and trust-token artefacts.
 *
 * <p>This package contains infrastructure-level model classes used to represent the security
 * information associated with business documents. The model covers the technical and legal
 * validation data required to establish trust in a document, as well as the metadata and artefacts
 * used when producing trust tokens for ASIC-S containers.
 *
 * <p>The package is focused on:
 * <ul>
 *   <li>business document validation results, including technical and legal trust decisions;
 *   <li>trust-token data structures and validation metadata;
 *   <li>certificate, signature, diagnostic, and validation-report information;
 *   <li>model objects used when assembling or describing signed ASIC-S container content.
 * </ul>
 */
package eu.ecodex.connector.infrastructure.outbound.security.model;
