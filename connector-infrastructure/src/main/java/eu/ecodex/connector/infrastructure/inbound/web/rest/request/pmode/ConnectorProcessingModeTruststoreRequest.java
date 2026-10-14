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

import eu.ecodex.connector.infrastructure.inbound.web.rest.validator.constraints.NotEmptyFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents the truststore configuration required for a connector processing mode.
 *
 * @param password       the password used to access the truststore (must not be blank, at most 255
 *                       characters)
 * @param truststoreFile the truststore file containing the trusted certificates (must not be null
 *                       or empty)
 */
@Builder
public record ConnectorProcessingModeTruststoreRequest(
    @NotBlank(message = "The truststore password must not be blank.")
    @Size(max = 255, message = "The [password] must not exceed 255 characters.")
    String password,

    @NotEmptyFile(message = "The [truststoreFile] must not be empty.")
    MultipartFile truststoreFile
) {
}
