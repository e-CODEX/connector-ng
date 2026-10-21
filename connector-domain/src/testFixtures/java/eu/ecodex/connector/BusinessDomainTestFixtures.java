/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import java.time.Instant;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class BusinessDomainTestFixtures {
    public static ConnectorBusinessDomain createDefaultBusinessDomain() {
        var builder = builder();
        return builder.build();
    }

    public static ConnectorBusinessDomain createdDefaultBusinessDomain() {
        return createDefaultBusinessDomain().toBuilder()
                                            .uuid("0ecd850c-3f8e-47a8-b95d-d56d336bb83a")
                                            .createdAt(Instant.now())
                                            .updatedAt(Instant.now())
                                            .build();
    }

    // is considered as an outgoing message
    private static ConnectorBusinessDomain.ConnectorBusinessDomainBuilder builder() {
        return ConnectorBusinessDomain
                .builder()
                .identifier(
                        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
                )
                .description("test business domain")
                .enabled(true)
                .source(ConnectorConfigurationSource.IMPLEMENTATION);
    }
}
