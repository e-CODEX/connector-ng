/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.pmode;

import eu.ecodex.connector.infrastructure.adapter.in.web.rest.validator.constraints.NotEmptyFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents a request to create a connector processing mode.
 *
 * @param businessDomainIdentifier the identifier of the business domain the processing mode is
 *                                 associated with (must not be blank)
 * @param description              a brief description of the processing mode (must not be blank, at
 *                                 most 512 characters)
 * @param processingModeFile       the uploaded processing mode file (must not be null or empty)
 * @param truststore               the optional {@link ConnectorProcessingModeTruststoreRequest}
 *                                 describing the truststore of the processing mode; may be
 *                                 {@code null}, and is validated recursively when present
 */
@Builder(toBuilder = true)
public record ConnectorProcessingModeCreationRequest(
    @NotBlank(message = "The [businessDomainIdentifier] must not be blank.")
    String businessDomainIdentifier,

    @NotBlank(message = "The [description] must not be blank.")
    @Size(max = 512, message = "The [description] must not exceed 512 characters.")
    String description,

    @NotEmptyFile(message = "The [processingModeFile] must not be empty.")
    MultipartFile processingModeFile,

    @Valid
    ConnectorProcessingModeTruststoreRequest truststore
) {
}
