/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.pmode;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Builder;

/**
 * The Service represents a service provided by the Connector.
 *
 * @param name The name of the service
 * @param type The type of the service
 */
@Builder(toBuilder = true)
public record ConnectorService(
        @NotBlank String name,
        @NotBlank String type
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format("{name=%s, type=%s}", name, type);
    }
}
