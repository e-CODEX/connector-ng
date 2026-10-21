/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import jakarta.annotation.Nonnull;

/**
 * Toolkit for handling security-related operations on {@link ConnectorBusinessMessage} instances
 * within the connector system.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Validating messages received from the gateway to ensure compliance with security
 *     policies and system requirements.</li>
 *     <li>Building secured message containers for messages sent to the gateway.</li>
 * </ul>
 */
public interface ConnectorSecurityToolkit {
    /**
     * Validates the given message to ensure that it adheres to the required system and security
     * policies, and returns the validated message.
     *
     * <p>Used to validate a message pushed to the connector system by the gateway.
     *
     * <p>If validation fails, the failure is reported by an exception.
     *
     * @param message the {@link ConnectorBusinessMessage} to validate (must not be null)
     *
     * @return the {@link ConnectorBusinessMessage} resulting from the validation; a new or enriched
     *     instance
     */
    ConnectorBusinessMessage validateMessage(@Nonnull ConnectorBusinessMessage message);

    /**
     * Builds a secured container for the given message, so that it is properly encapsulated for
     * transport in line with the defined security standards.
     *
     * <p>Used to build message containers for messages pushed to the gateway by the connector.
     *
     * @param message the {@link ConnectorBusinessMessage} to encapsulate in a secured container
     *                (must not be null)
     *
     * @return the {@link ConnectorBusinessMessage} encapsulated in a secured container, with any
     *     modifications required for compliance with the system standards
     */
    ConnectorBusinessMessage buildContainer(@Nonnull ConnectorBusinessMessage message);
}
