/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.outbound.step;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * A processing step in the connector's outbound message workflow that creates and assigns a unique
 * EBMS message ID to the specified {@link ConnectorMessage}, if the EBMS ID generator is enabled in
 * the configuration.
 *
 * <p>This step is responsible for handling the message processing configuration and interacting
 * with the {@link ConnectorMessageService} to manage the assignment of the EBMS message ID. The
 * presence or absence of an EBMS message ID is dictated by the configuration properties, allowing
 * for flexibility in the connector's behaviour.
 */
@Slf4j
@DomainService
public class ConnectorOutboundMessageEbmsIdStep implements ConnectorMessageStep {
    private final ConnectorMessageService messageService;
    private final ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider;

    /**
     * Constructs a new instance of {@code ConnectorOutboundMessageEbmsIdStep}.
     *
     * @param messageService                  the {@link ConnectorMessageService} instance used to
     *                                        manage and persist {@link ConnectorMessage} data. This
     *                                        is used for assigning the EBMS message ID to an
     *                                        outbound message.
     * @param messageProcessingConfigProvider the {@link ConnectorMessageProcessingConfigProvider}
     *                                        instance providing configuration details for message
     *                                        processing, including whether the EBMS ID generator is
     *                                        enabled.
     */
    public ConnectorOutboundMessageEbmsIdStep(
            ConnectorMessageService messageService,
            ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider) {
        this.messageService = messageService;
        this.messageProcessingConfigProvider = messageProcessingConfigProvider;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage message) {
        log.info("creating EBMS message ID for outbound connector message: [{}]", message);

        var processingProperties = this.messageProcessingConfigProvider.getProcessingProperties();

        if (processingProperties.ebmsIdGeneratorEnabled()) {
            log.info(
                    "EBMS message ID generator enabled, generating new EBMS message ID for "
                    + "outbound connector message: [{}]", message
            );

            return this.messageService.assignEbmsIdentifier(message);
        }

        log.info(
                "EBMS message ID generator disabled, skipping EBMS message ID generation for "
                + "outbound connector message: [{}]",
                message
        );

        return message;
    }
}
