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
 * Provides outbound infrastructure components for communication with external systems and
 * services.
 *
 * <p>This package contains adapters and technical implementations responsible for sending data
 * outside the connector boundary, including transport, database, protocol-specific integrations,
 * and external service communication.
 *
 * <p>Subpackages may provide support for specific outbound technologies such as:
 * </p>
 * <ul>
 *   <li>SOAP-based communication</li>
 *   <li>External API integrations</li>
 *   <li>Security and protocol interoperability concerns</li>
 *   <li>Database</li>
 *   <li>...</li>
 * </ul>
 *
 * <p>The package belongs to the infrastructure layer and should contain only
 * technical implementation details, separated from domain and application
 * business logic.
 */
package eu.ecodex.connector.infrastructure.outbound;
