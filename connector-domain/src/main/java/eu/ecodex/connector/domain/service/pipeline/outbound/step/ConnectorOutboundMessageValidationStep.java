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
import eu.ecodex.connector.domain.api.service.ConnectorProcessingModeService;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.property.ConnectorMessageProcessingProperties;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a processing step for checking outbound connector messages as part of the message
 * workflow.
 *
 * <p>This class implements {@link ConnectorMessageStep} and performs validations
 * for outgoing messages by using supporting services. It ensures that the messages adhere to the
 * required business and processing rules before they are moved further in the processing chain.
 *
 * <p>The processing step leverages the following services:
 * <ul>
 *     <li> {@link ConnectorMessageService}: Used for verifying and handling specific
 *     message-related requirements, such as checking the party information.
 *     <li> {@link ConnectorProcessingModeService}: Ensures compliance with the processing mode
 *     defined for the message, including verification based on the outgoing message verification
 *     mode.
 *     <li> {@link ConnectorMessageProcessingConfigProvider}: Supplies configuration properties
 *     necessary for processing.
 * </ul>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *     <li> Validate that party information in the message is correct using the
 *     {@code checkPartiesInfo} method of {@link ConnectorMessageService}.
 *     <li> Confirm that the message adheres to the configured processing mode, calling the
 *     {@code checkMessage} method of {@link ConnectorProcessingModeService}.
 * </ul>
 */
@Slf4j
@DomainService
public class ConnectorOutboundMessageValidationStep implements ConnectorMessageStep {
    private final ConnectorMessageService messageService;
    private final ConnectorProcessingModeService processingModeService;
    private final ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider;

    /**
     * Constructs an instance of {@code ConnectorOutboundMessageValidationStep}, which represents a
     * processing step for validating outbound connector messages as part of the message workflow.
     *
     * @param messageService                  The instance of {@link ConnectorMessageService} used
     *                                        for performing message-related operations, such as
     *                                        validating party information in the outgoing message.
     * @param processingModeService           The instance of {@link ConnectorProcessingModeService}
     *                                        used to validate that the processing mode for the
     *                                        outgoing message complies with the defined rules.
     * @param messageProcessingConfigProvider The provider of
     *                                        {@link ConnectorMessageProcessingProperties} used to
     *                                        supply configuration values required for processing.
     */
    public ConnectorOutboundMessageValidationStep(
            ConnectorMessageService messageService,
            ConnectorProcessingModeService processingModeService,
            ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider) {
        this.messageService = messageService;
        this.processingModeService = processingModeService;
        this.messageProcessingConfigProvider = messageProcessingConfigProvider;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage outboundMessage) {
        log.debug("processing outbound message validation for: [{}]", outboundMessage);

        var processingProperties = this.messageProcessingConfigProvider.getProcessingProperties();
        this.messageService.checkPartiesInfo(outboundMessage);
        this.processingModeService.checkMessage(
                outboundMessage, processingProperties.outboundMessageVerificationMode()
        );

        log.debug("outbound message validation completed for: [{}]", outboundMessage);

        return outboundMessage;
    }
}
