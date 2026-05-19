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
 * Provides outbound SOAP infrastructure components used by the connector to communicate with
 * external SOAP-based services.
 *
 * <p>This package contains the technical integration layer responsible for:
 * <ul>
 *   <li>SOAP client configuration and initialization</li>
 *   <li>SOAP protocol handling and serialization</li>
 *   <li>Transport-level security and interoperability concerns</li>
 * </ul>
 *
 * <p>The classes in this package belong to the infrastructure layer and should remain independent
 * of business-specific logic.
 */
package eu.ecodex.connector.infrastructure.outbound.soap;
