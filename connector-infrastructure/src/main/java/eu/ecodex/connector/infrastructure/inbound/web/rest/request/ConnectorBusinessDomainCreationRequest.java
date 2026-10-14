/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request;

import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Represents a request for creating a new business domain within the connector system.
 *
 * @param identifier  the unique identifier of the business domain (must not be blank, at most 255
 *                    characters)
 * @param description a textual description of the business domain (must not be blank, at most 512
 *                    characters)
 * @param enabled     whether the business domain is active ({@code true}) or disabled
 *                    ({@code false}) (must not be null)
 * @param source      the {@link ConnectorConfigurationSource} identifying the origin of the
 *                    configuration data of the business domain (must not be null)
 */
@Builder
public record ConnectorBusinessDomainCreationRequest(
    @Size(max = 255, message = "The [identifier] must not exceed 255")
    @NotBlank(message = "The [identifier] must not be blank.")
    String identifier,

    @Size(max = 512, message = "The [description] must not exceed 512")
    @NotBlank(message = "The [description] must not be blank.")
    String description,

    @NotNull(message = "The [enabled] flag must not be null.")
    Boolean enabled,

    @NotNull(message = "The [source] must not be null.")
    ConnectorConfigurationSource source
) {
}
