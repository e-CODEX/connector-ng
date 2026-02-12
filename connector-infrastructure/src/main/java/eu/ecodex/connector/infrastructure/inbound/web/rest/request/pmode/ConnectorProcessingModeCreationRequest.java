/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.pmode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Represents a request to create a connector processing mode. This record encapsulates essential
 * metadata required for defining a processing mode within the connector system, including its
 * description, associated business domain, and truststore details.
 *
 * @param description              A brief description of the processing mode.
 * @param businessDomainIdentifier The identifier of the business domain associated with the
 *                                 processing mode.
 * @param truststore               The truststore configuration required for the processing mode,
 *                                 encapsulated in a {@link ConnectorKeystoreCreationRequest}.
 */
@Builder
public record ConnectorProcessingModeCreationRequest(
        @NotBlank String description,
        @NotBlank String businessDomainIdentifier,
        @NotNull ConnectorKeystoreCreationRequest truststore
) {
}
