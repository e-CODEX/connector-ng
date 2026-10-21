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
 * Application services implementing the link use cases of the connector.
 *
 * <p>Services in this package implement the link-related inbound ports of
 * {@link eu.ecodex.connector.application.port.api.link}. They orchestrate operations on the links
 * between the connector and its link partners, such as configuring and retrieving them, by
 * delegating business rules to the domain model and persistence or gateway access to outbound
 * ports.
 */
package eu.ecodex.connector.application.service.link;
