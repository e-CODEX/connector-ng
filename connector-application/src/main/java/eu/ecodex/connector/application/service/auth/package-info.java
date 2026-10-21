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
 * Application services implementing the user management use cases of the connector.
 *
 * <p>Services in this package implement the user-related inbound ports of
 * {@link eu.ecodex.connector.application.port.api.auth}. They orchestrate the management of the
 * users who access the connector (for example the admin UI), such as creating, updating, retrieving
 * and deactivating accounts, by delegating business rules to the domain model and persistence to
 * outbound ports.
 */
package eu.ecodex.connector.application.service.auth;
