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
 * Domain model of the e-CODEX Connector: its entities, value objects and enumerations.
 *
 * <p>Classes in this package express the connector's business concepts (messages and their
 * metadata, attachments, evidences, link partners, business domains, P-Modes) as plain Java types.
 * They carry the state and invariants of those concepts and know nothing about persistence,
 * transport or any other technical concern.
 *
 * <p>Typical content:
 *
 * <ul>
 *   <li>entities, identified by their identity and carrying business behavior;
 *   <li>value objects, immutable and compared by value;
 *   <li>enumerations of domain-specific states and types (for example message or evidence types).
 * </ul>
 *
 * <p>The model is free of framework dependencies and depends on no other layer, which keeps it
 * portable and testable in isolation.
 */
package eu.ecodex.connector.domain.model;
