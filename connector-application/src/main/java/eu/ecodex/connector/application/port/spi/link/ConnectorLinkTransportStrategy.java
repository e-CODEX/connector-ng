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
 * Defines the strategy for transporting a connector message to a link partner.
 *
 * <p>Implementations encapsulate the logic for sending messages between components or systems
 * within the connector architecture, for example between backend systems and gateways. They are
 * responsible for managing the communication with the link partner and for handling the delivery of
 * the given message.
 *
 * <p>Key responsibilities:
 * <ul>
 *     <li>Define how a message is transported to a link partner.</li>
 *     <li>Support various transport mechanisms or protocols, depending on the link
 *     configuration.</li>
 *     <li>Respect the operational requirements and settings of the link partner.</li>
 * </ul>
 *
 * <p>Implementations are also responsible for handling error scenarios, retries and
 * acknowledgement mechanisms, as required by the specific communication context.
 */
public interface ConnectorLinkTransportStrategy {
    /**
     * Transports the given message to the link partner it is addressed to.
     *
     * <p>The method returns normally if the message was handed over to the transport successfully.
     * If transport fails, the failure is reported by an exception.
     *
     * @param message the connector message to transport (must not be null)
     */
    void transport(@Nonnull ConnectorMessage message);
}
