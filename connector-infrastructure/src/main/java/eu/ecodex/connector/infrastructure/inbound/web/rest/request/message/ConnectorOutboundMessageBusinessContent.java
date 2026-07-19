/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.message;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents the business content of an outbound message, comprising a primary business document
 * and its associated content file.
 *
 * @param contentFile      the multipart file that represents the actual business content; must not
 *                         be null
 * @param businessDocument the {@link ConnectorOutboundMessageBusinessDocument} containing metadata
 *                         and additional details regarding the business document; must not be null
 */
@Builder(toBuilder = true)
public record ConnectorOutboundMessageBusinessContent(
    @NotNull(message = "The business content file must not be null.")
    MultipartFile contentFile,
    @NotNull(message = "The business document must not be null.")
    ConnectorOutboundMessageBusinessDocument businessDocument
) {
}
