/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.businessdomain;

import jakarta.annotation.Nonnull;
import lombok.Builder;


/**
 * Represents an uuid used to uniquely define a business domain within the connector system.
 *
 * <p>This uuid is used across the connector system to associate configurations, properties,
 * and features
 * with a particular business domain. It is integral to the separation of data and behaviour for
 * different domains.
 *
 * @param messageLaneIdentifier A unique string uuid representing the business domain's
 *                              message routing lane.
 */
@Builder
public record ConnectorBusinessDomainIdentifier(
        @Nonnull String messageLaneIdentifier
) {
    @Override
    @Nonnull public String toString() {
        return String.format("{messageLaneIdentifier=%s}", messageLaneIdentifier);
    }
}
