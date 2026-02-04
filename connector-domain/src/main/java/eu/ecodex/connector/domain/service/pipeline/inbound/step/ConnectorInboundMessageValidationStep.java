/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.inbound.step;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.api.service.ConnectorProcessingModeService;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a validation step in the connector message processing workflow.
 *
 * <p>The class is responsible for validating incoming {@link ConnectorMessage} instances
 * based on the processing mode defined in the connector system configuration. It implements
 * the {@link ConnectorMessageStep} interface and ensures messages meet predefined
 * requirements before progressing further in the processing workflow.
 */
@Slf4j
@DomainService
public class ConnectorInboundMessageValidationStep implements ConnectorMessageStep {
    private final ConnectorProcessingModeService processingModeService;
    private final ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider;

    /**
     * Constructs a new instance of the {@code ConnectorInboundMessageValidationStep} class.
     *
     * @param processingModeService           the service responsible for managing and validating
     *                                        processing modes associated with the connector.
     * @param messageProcessingConfigProvider the configuration provider that supplies properties
     *                                        for processing connector messages.
     */
    public ConnectorInboundMessageValidationStep(
            ConnectorProcessingModeService processingModeService,
            ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider) {
        this.processingModeService = processingModeService;
        this.messageProcessingConfigProvider = messageProcessingConfigProvider;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage inboundMessage) {
        log.debug("processing incoming message validation for: [{}]", inboundMessage);

        var processingProperties = this.messageProcessingConfigProvider.getProcessingProperties();
        this.processingModeService.checkMessage(
                inboundMessage, processingProperties.inboundMessageVerificationMode()
        );

        log.debug("incoming message validation completed for: [{}]", inboundMessage);

        return inboundMessage;
    }
}
