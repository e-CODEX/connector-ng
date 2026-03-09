/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message.outbound.pipeline.step;

import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.api.ConnectorSecurityToolkit;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Represents a processing step in the outbound message workflow for the connector system, aimed at
 * ensuring the security of outgoing messages before they are sent or processed further. This class
 * is responsible for wrapping an outbound {@link ConnectorMessage} into a secured container to
 * comply with the connector's security and transport standards.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Uses the {@link ConnectorSecurityToolkit} to encapsulate the outgoing message into a
 *       secure container.
 *   <li>Ensures that the outgoing message adheres to the required security policies before
 *       transmission.
 * </ul>
 *
 * <p>By implementing the {@link ConnectorMessageStep} interface, this class serves as a
 * modular step in the message processing pipeline, enhancing the security posture of the system.
 *
 * <p>Once the outgoing message is processed, it is returned as a secured container that is ready
 * for further processing or transmission.
 *
 * <p>Dependencies:
 * <ul>
 *   <li>{@link ConnectorSecurityToolkit}: Provides the necessary methods to build a secured
 *       container from a {@link ConnectorMessage}.</li>
 * </ul>
 */
@Slf4j
@Component
public class ConnectorOutboundMessageSecurityStep implements ConnectorMessageStep {
    private final ConnectorSecurityToolkit securityToolkit;

    /**
     * Constructs a new instance of {@code ConnectorOutboundMessageSecurityStep}.
     *
     * <p>This constructor initializes the security step with the provided
     * {@link ConnectorSecurityToolkit}, which is used to handle the creation of secured
     * containers for outbound messages.
     *
     * @param securityToolkit the {@link ConnectorSecurityToolkit} instance responsible for
     *                        executing security-related operations on {@link ConnectorMessage}
     *                        instances, such as building secured message containers.
     */
    public ConnectorOutboundMessageSecurityStep(ConnectorSecurityToolkit securityToolkit) {
        this.securityToolkit = securityToolkit;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage outboundMessage) {
        log.debug("processing outbound message ASIC-S container for: [{}]", outboundMessage);

        // create the ASIC-S container
        var containerMessage = this.securityToolkit.buildContainer(outboundMessage);

        // TODO save ASIC-S container as attachment

        log.debug("ASIC-S container created for: [{}]", outboundMessage);

        return containerMessage;
    }
}
