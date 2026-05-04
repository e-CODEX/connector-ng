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
 * Contains JPA entity mappings for outbound database persistence.
 *
 * <p>This package defines the persistence model used by the outbound infrastructure to represent
 * outbound messages, related metadata, processing state, and other database-backed records. These
 * entities are part of the infrastructure layer and are intended to be used by repositories and
 * persistence services rather than exposed directly to higher application or domain layers.
 *
 * <p>Entity classes in this package should remain focused on database mapping concerns and should
 * not contain business workflow logic beyond what is required to maintain persistence consistency.
 */
package eu.ecodex.connector.infrastructure.outbound.database.entity;
