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
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents the truststore configuration required for a connector's processing mode. This record
 * encapsulates the details of the truststore, including its password and the associated truststore
 * file.
 *
 * @param password       The password used to access the truststore. It must not be null.
 * @param truststoreFile The truststore file containing the necessary certificates. It must not be
 *                       null.
 */
@Builder
public record ConnectorProcessingModeTruststoreRequest(
    @NotBlank(message = "The truststore password must not be empty.")
    String password,
    @NotNull(message = "The truststore file must not be empty.")
    MultipartFile truststoreFile
) {
}
