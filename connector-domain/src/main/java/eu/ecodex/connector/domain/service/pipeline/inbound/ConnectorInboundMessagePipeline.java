/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.inbound;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.link.ConnectorLinkSubmissionService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.util.ConnectorBusinessDomainUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;


/**
 * Represents an inbound message processing pipeline for a connector system.
 *
 * <p>This class orchestrates the processing of inbound {@link ConnectorMessage} instances by
 * executing a predefined sequence of processing steps. The pipeline ensures that messages are
 * validated, checked for security compliance, processed for acceptance, and submitted to their
 * intended backends or endpoints. In the event of processing failures, the pipeline also handles
 * non-delivery processing.
 *
 * <p>Additionally, the {@code ConnectorInboundMessagePipeline} utilizes a
 * {@link ConnectorLinkSubmissionService} to submit processed messages for further actions or
 * routing.
 */
@Slf4j
@DomainService
public class ConnectorInboundMessagePipeline implements ConnectorMessagePipeline {
    // IMPORTANT: The names of the steps are important! They should correspond to the names of
    //  the implementation classes, but in camel case.
    private final ConnectorMessageStep connectorInboundMessageValidationStep;
    private final ConnectorMessageStep connectorInboundMessageBackendValidationStep;
    private final ConnectorMessageStep connectorInboundMessageAcceptanceStep;
    private final ConnectorMessageStep connectorInboundMessageSecurityStep;
    private final ConnectorMessageStep connectorInboundMessageNonDeliveryStep;
    private final ConnectorLinkSubmissionService linkSubmissionService;

    /**
     * Constructs an instance of {@code ConnectorInboundMessagePipeline} to facilitate the handling
     * of inbound messages within the connector system through a series of processing steps.
     *
     * @param connectorInboundMessageValidationStep        the step responsible for validating
     *                                                     inbound messages.
     * @param connectorInboundMessageBackendValidationStep the step responsible for backend-specific
     *                                                     validation of inbound messages.
     * @param connectorInboundMessageAcceptanceStep        the step responsible for message
     *                                                     acceptance checks.
     * @param connectorInboundMessageSecurityStep          the step responsible for ensuring the
     *                                                     security of inbound messages.
     * @param connectorInboundMessageNonDeliveryStep       the step responsible for handling
     *                                                     non-delivery processing of messages.
     * @param linkSubmissionService                        the service for submitting processed
     *                                                     messages to the appropriate endpoint or
     *                                                     system.
     */
    public ConnectorInboundMessagePipeline(
            ConnectorMessageStep connectorInboundMessageValidationStep,
            ConnectorMessageStep connectorInboundMessageBackendValidationStep,
            ConnectorMessageStep connectorInboundMessageAcceptanceStep,
            ConnectorMessageStep connectorInboundMessageSecurityStep,
            ConnectorMessageStep connectorInboundMessageNonDeliveryStep,
            ConnectorLinkSubmissionService linkSubmissionService) {
        this.connectorInboundMessageValidationStep = connectorInboundMessageValidationStep;
        this.connectorInboundMessageBackendValidationStep =
                connectorInboundMessageBackendValidationStep;
        this.connectorInboundMessageAcceptanceStep = connectorInboundMessageAcceptanceStep;
        this.connectorInboundMessageSecurityStep = connectorInboundMessageSecurityStep;
        this.connectorInboundMessageNonDeliveryStep = connectorInboundMessageNonDeliveryStep;
        this.linkSubmissionService = linkSubmissionService;
    }

    @Override
    public void process(@NonNull ConnectorMessage message) {
        log.debug("start processing inbound message: [{}]", message);

        try {
            ConnectorBusinessDomainUtil.setCurrentBusinessDomain(
                    message.businessDomainIdentifier()
            );
            // TODO see when the message should be updated in the db
            var inboundMessage = this.connectorInboundMessageValidationStep.execute(message);
            inboundMessage = this.connectorInboundMessageBackendValidationStep.execute(
                    inboundMessage);
            // TODO: decide if this should maybe generated after msg successfully transported
            //  to backend link?
            inboundMessage = this.connectorInboundMessageAcceptanceStep.execute(inboundMessage);
            // submit back RELAY_REMMD_ACCEPTANCE evidence message to the gateway
            this.linkSubmissionService.submit(inboundMessage);
            // resolve the ASIC-S container
            inboundMessage = this.connectorInboundMessageSecurityStep.execute(inboundMessage);
            // submit to the backend
            this.linkSubmissionService.submit(inboundMessage);

            log.info(
                    "message with backend id [{}] has been successfully submitted to backend [{}]",
                    inboundMessage.backendMessageIdentifier(), inboundMessage.backendName()
            );
        } catch (Exception e) { // TODO improve the exception handling (sec or container exc)
            log.warn(
                    "security exception during inbound message [{}] processing! responding with "
                    + "RelayRemmdRejection confirmation message",
                    message, e
            );

            var relayRemmdRejectionMessage = this.connectorInboundMessageNonDeliveryStep.execute(
                    message
            );
            // submit back NON_DELIVERY evidence message to the gateway
            this.linkSubmissionService.submit(relayRemmdRejectionMessage);

            log.warn(
                    "incoming message [{}] from gateway to backend has been rejected due to "
                    + "security exception",
                    message
            );
        } finally {
            ConnectorBusinessDomainUtil.setCurrentBusinessDomain(null);
        }
    }
}
