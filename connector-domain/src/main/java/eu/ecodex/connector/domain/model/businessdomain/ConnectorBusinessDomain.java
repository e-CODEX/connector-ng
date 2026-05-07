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

import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;

/**
 * Represents a business domain in the connector system.
 *
 * <p>This class is used to identify, describe, and manage settings specific to a business domain,
 * enabling the segmentation of configurations within the connector.
 *
 * <p>The {@code ConnectorBusinessDomain} includes attributes such as an uuid, description,
 * whether the business domain is enabled, associated properties, and its configuration source.
 *
 * <p>The class provides predefined constants for a default business domain, which can serve as a
 * general fallback configuration.
 *
 * @param uuid        A unique string uuid representing the business domain.
 * @param identifier  The unique identifier for the business domain.
 * @param description A textual description of the business domain.
 * @param enabled     A boolean flag indicating if the business domain is active or disabled.
 * @param source      Specifies the configuration source for this business domain, such as database
 *                    or environment.
 * @param createdAt   The timestamp when the business domain was created.
 * @param updatedAt   The timestamp when the business domain was last updated.
 */
@Builder
public record ConnectorBusinessDomain(
        String uuid,
        @NotBlank ConnectorBusinessDomainIdentifier identifier,
        @NotBlank String description, boolean enabled,
        ConnectorConfigurationSource source,
        Instant createdAt,
        Instant updatedAt
) {
    public static final ConnectorBusinessDomainIdentifier DEFAULT_BUSINESS_DOMAIN_ID =
            ConnectorBusinessDomainIdentifier
                    .builder()
                    .messageLaneIdentifier("default_business_domain")
                    .build();

    public static final ConnectorBusinessDomain DEFAULT_BUSINESS_DOMAIN =
            ConnectorBusinessDomain.builder()
                                   .identifier(DEFAULT_BUSINESS_DOMAIN_ID)
                                   .description("Default Business Domain")
                                   .source(ConnectorConfigurationSource.APPLICATION)
                                   .enabled(true)
                                   .build();
}
