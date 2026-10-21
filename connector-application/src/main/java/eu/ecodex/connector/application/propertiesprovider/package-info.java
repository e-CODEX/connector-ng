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
 * Provides the application layer with access to the connector's configuration properties.
 *
 * <p>Components in this package read connector properties from their underlying source, group
 * them into typed, application-level views and make them available to use cases and services, so
 * that the rest of the application layer does not depend on how or where the configuration is
 * stored.
 *
 * <p>This package exposes and maps properties only: it must not contain business rules, and it
 * should not leak framework-specific configuration types into the domain model.
 */
package eu.ecodex.connector.application.propertiesprovider;
