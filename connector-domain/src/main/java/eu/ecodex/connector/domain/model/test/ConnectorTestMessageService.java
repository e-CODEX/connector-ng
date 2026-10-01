/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.test;

import lombok.Builder;

/**
 * Represents a message service used by the connector system.
 *
 * @param name The name identifying the message service.
 * @param type The type of the message service.
 */
@Builder
public record ConnectorTestMessageService(
    String name,
    String type
) {
}
