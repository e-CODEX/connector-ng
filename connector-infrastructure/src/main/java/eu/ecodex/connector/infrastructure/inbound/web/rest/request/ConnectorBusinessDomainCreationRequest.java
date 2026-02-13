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
import lombok.Builder;

/**
 * Represents a request for creating a new business domain within the connector system.
 *
 * <p>This record captures the necessary information required to define and register a new
 * business domain. It is used as part of the request payload for administrative operations related
 * to business domain management.
 *
 * @param identifier  The unique identifier for the business domain.
 * @param description A textual description of the business domain.
 * @param enabled     A boolean flag indicating if the business domain is active or disabled.
 * @param source      Identifies the configuration source associated with the business domain. The
 *                    value must be non-null and represents the origin of configuration data.
 */
@Builder
public record ConnectorBusinessDomainCreationRequest(
        @NotBlank(message = "Business domain identifier must not be blank.")
        String identifier,
        @NotBlank(message = "Business domain description must not be blank.")
        String description,
        @NotNull(message = "Enabled flag must not be null.")
        Boolean enabled,
        @NotNull(message = "Configuration source must not be null.")
        ConnectorConfigurationSource source
) {
}
