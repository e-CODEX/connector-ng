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

import eu.ecodex.connector.domain.model.keystore.ConnectorKeystoreType;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Builder;

/**
 * Represents a keystore used by the Domibus connector.
 *
 * @param password    The password of the keystore (plain text.
 * @param description The description of the keystore.
 * @param type        The type of the keystore.
 */
@Builder
public record ConnectorKeystoreCreationRequest(
        @NotBlank(message = "Keystore password must not be blank.")
        String password,
        @NotBlank(message = "Keystore description must not be blank.")
        String description,
        @NotNull(message = "Keystore type must not be null.")
        @Nonnull ConnectorKeystoreType type
) implements Serializable {
}
