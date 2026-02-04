/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.as4;

import lombok.Builder;

/**
 * Represents an AS4 service in the connector domain. This class is used to define and categorize
 * specific services associated with the AS4 communication protocol.
 *
 * <p>An AS4 service typically encapsulates a logical grouping or classification that aids in the
 * identification and handling of AS4-based communication processes or operations.
 *
 * @param name The name of the AS4 service, providing a unique uuid or label.
 * @param type The type of the AS4 service, defining its category or classification.
 */
@Builder
public record ConnectorAS4Service(
        String name,
        String type
) {
}
