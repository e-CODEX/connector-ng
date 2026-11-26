/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.transport;

import eu.ecodex.connector.application.exception.message.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.application.exception.message.ConnectorMessageTransportStepRetryException;
import eu.ecodex.connector.application.port.api.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.port.api.transport.ConnectorRetryTransportStep;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link ConnectorRetryTransportStep}.
 */
@Slf4j
@Service
@Transactional
public class ConnectorRetryTransportStepService implements ConnectorRetryTransportStep {
    private final ConnectorLinkSubmitter linkSubmitter;
    private final ConnectorMessageTransportStepRepository transportStepRepository;

    /**
     * Create a service used to submit a transport step retry.
     *
     * @param linkSubmitter           the service used to submit a message
     * @param transportStepRepository the repository that handle transport step persistence
     */
    public ConnectorRetryTransportStepService(
        ConnectorLinkSubmitter linkSubmitter,
        ConnectorMessageTransportStepRepository transportStepRepository) {
        this.linkSubmitter = linkSubmitter;
        this.transportStepRepository = transportStepRepository;
    }

    @Override
    public void execute(@NonNull String transportIdentifier) {
        var step = transportStepRepository.findByIdentifier(transportIdentifier);

        if (step == null) {
            throw new ConnectorMessageTransportStepNotFoundException(
                "No transport step found with identifier %s".formatted(transportIdentifier)
            );
        }

        if (!step.isRetryable()) {
            throw new ConnectorMessageTransportStepRetryException(
                "The message with transport id [" + transportIdentifier + "] already "
                    + step.status().name() + " and is not retryable!"
            );
        }

        // get transported message
        var transportedMessage = step.transportedMessage();
        log.info(
            "Retry transport step [{}] message submission to [{}]",
            transportIdentifier, step.linkPartnerName()
        );

        try {
            // retry should submit message to the gateway or backend
            this.linkSubmitter.submit(transportedMessage);

            log.info(
                "Transport step message with id: [{}] successfully submitted back to the "
                    + "delivery queue", transportedMessage.identifier()
            );
        } catch (Exception e) {
            log.error("Retry mechanism failed for transport step id: [{}]", transportIdentifier, e);
            var reason = "Retry mechanism failed for transport step id: [%s]";
            throw new ConnectorMessageTransportStepRetryException(
                reason.formatted(transportIdentifier), e);
        }
    }
}
