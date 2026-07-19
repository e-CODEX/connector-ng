/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.link;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Defines the strategy for transporting a connector message to a specified link partner.
 *
 * <p>The {@code ConnectorLinkTransportStrategy} interface encapsulates the logic for sending
 * messages between components or systems within the connector architecture. Implementations of this
 * interface are responsible for managing the communication and ensuring the successful delivery of
 * the provided message to the designated partner.
 *
 * <p>Key Responsibilities:
 * - Define how a message is transported to a link partner. - Support various transport mechanisms
 * or protocols based on the provided link configuration. - Ensure compatibility with the
 * operational requirements and settings of the link partner.
 *
 * <p>Primary use cases include enabling communication between backend systems or gateways,
 * using the defined strategies according to the operational modes of the connector.
 *
 * <p>Implementations must provide logic for handling message delivery, including potential error
 * scenarios, retries, and acknowledgement mechanisms as deemed necessary for the specific
 * communication context.
 */
public interface ConnectorLinkTransportStrategy {
    void transport(@Nonnull ConnectorMessage message);
}
