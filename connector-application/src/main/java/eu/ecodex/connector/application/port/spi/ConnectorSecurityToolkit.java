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

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Interface defining a toolkit for handling security-related operations on {@link ConnectorMessage}
 * instances within the Connector system.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Validating messages to ensure compliance with security policies and system requirements.
 *     <li>Building secured message containers for transportation or storage.
 * </ul>
 */
public interface ConnectorSecurityToolkit {
    /**
     * Validates the provided {@link ConnectorMessage} to ensure that it adheres to the required
     * system and security policies.
     *
     * <p>Used to validate a message pushed to the connector system by the gateway.
     *
     * @param message the {@link ConnectorMessage} to be validated, containing all relevant data for
     *                verification of compliance against defined standards.
     */
    void validateMessage(@Nonnull ConnectorMessage message);

    /**
     * Constructs a secured container for the specified {@link ConnectorMessage}. This method is
     * responsible for building a message container that complies with the defined security
     * standards and system requirements, ensuring the message is encapsulated properly for
     * transportation or storage purposes.
     *
     * <p>Used to build message containers for messages pushed to the gateway system by the
     * connector.
     *
     * @param message the {@link ConnectorMessage} to be encapsulated in a secured container,
     *                containing the details necessary for transport or storage.
     *
     * @return the {@link ConnectorMessage} instance, encapsulated in a secured container with any
     *     enhancements or modifications required for compliance with system standards.
     */
    ConnectorMessage buildContainer(@Nonnull ConnectorMessage message);
}
