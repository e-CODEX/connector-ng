/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.container.checks;


import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainer;
import jakarta.annotation.Nonnull;

/**
 * Represents an interface for performing checks or validations on the {@link ConnectorContainer}.
 */
public interface ConnectorMessageContainerChecker {
    /**
     * Performs validation or checks on the provided {@link ConnectorContainer} instance.
     *
     * @param container the {@link ConnectorContainer} holding the necessary artifacts for connector
     *                  message exchange. Must not be null.
     */
    void check(@Nonnull ConnectorContainer container);
}
