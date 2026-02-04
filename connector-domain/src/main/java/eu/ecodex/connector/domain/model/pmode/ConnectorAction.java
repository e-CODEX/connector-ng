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
 * The Action class represents an action associated with a message in the system.
 *
 * <p>Instances of this class are used to store the action information of a message in the
 * PMode.
 *
 * @param name The name of the action.
 */
@Builder(toBuilder = true)
public record ConnectorAction(
        @NotBlank String name
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format("{name=%s}", name);
    }
}
