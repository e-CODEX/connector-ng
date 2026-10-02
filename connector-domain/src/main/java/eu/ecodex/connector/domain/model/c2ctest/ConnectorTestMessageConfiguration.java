/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.c2ctest;

import lombok.Builder;

/**
 * Represents the configuration used for sending connector test messages.
 *
 * @param enabled Indicates whether sending connector test messages is enabled.
 * @param service The message service configuration used for connector test messages.
 * @param action  The action to perform when sending a connector test message.
 */
@Builder
public record ConnectorTestMessageConfiguration(
    boolean enabled,
    ConnectorTestMessageService service,
    String action
) {
}
