/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message.inbound.pipeline;

import eu.ecodex.connector.application.service.usecase.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
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
 * {@link ConnectorLinkSubmitter} to submit processed messages for further actions or
 * routing.
 */
@Slf4j
public class ConnectorInboundMessagePipeline implements ConnectorMessagePipeline {
    private final ConnectorMessageStep backendNameStep;
    private final ConnectorMessageStep acceptanceStep;
    private final ConnectorMessageStep securityStep;
    private final ConnectorMessageStep nonDeliveryStep;
    private final ConnectorMessageStep linkSubmissionStep;

    /**
     * Constructs an instance of {@code ConnectorInboundMessagePipeline} to facilitate the handling
     * of inbound messages within the connector system through a series of processing steps.
     *
     * @param backendNameStep       the step responsible for backend-specific validation of inbound
     *                              messages.
     * @param acceptanceStep        the step responsible for message acceptance checks.
     * @param securityStep          the step responsible for ensuring the security of inbound
     *                              messages.
     * @param nonDeliveryStep       the step responsible for handling non-delivery processing of
     *                              messages.
     * @param linkSubmissionStep    the service responsible for submitting the processed message to
     *                              the appropriate endpoint or next stage; must not be null.
     */
    public ConnectorInboundMessagePipeline(
            ConnectorMessageStep backendNameStep,
            ConnectorMessageStep acceptanceStep,
            ConnectorMessageStep securityStep,
            ConnectorMessageStep nonDeliveryStep,
            ConnectorMessageStep linkSubmissionStep) {
        this.backendNameStep = backendNameStep;
        this.acceptanceStep = acceptanceStep;
        this.securityStep = securityStep;
        this.nonDeliveryStep = nonDeliveryStep;
        this.linkSubmissionStep = linkSubmissionStep;
    }

    @Override
    public void process(@NonNull ConnectorMessage message) {
        log.info("Start processing inbound message: [{}]", message.identifier());

        try {
            ConnectorBusinessDomainUtil.setCurrentBusinessDomain(
                    message.businessDomainIdentifier()
            );
            var inboundMessage = this.backendNameStep.execute(message);
            // TODO: decide if this should maybe generated after msg successfully transported
            //  to backend link?
            var acceptanceMessage = this.acceptanceStep.execute(inboundMessage);
            // submit back RELAY_REMMD_ACCEPTANCE evidence message to the gateway
            this.linkSubmissionStep.execute(acceptanceMessage);
            // resolve the ASIC-S container
            inboundMessage = this.securityStep.execute(inboundMessage);
            // submit to the backend
            this.linkSubmissionStep.execute(inboundMessage);

            log.info(
                    "Message with identifier [{}] has been successfully submitted to backend [{}]",
                    inboundMessage.identifier(), inboundMessage.backendName()
            );
        } catch (Exception e) { // TODO improve the exception handling (sec or container exc)
            log.warn(
                    "Security exception during inbound message [{}] processing! responding with "
                    + "RelayRemmdRejection confirmation message",
                    message.identifier(), e
            );

            var relayRemmdRejectionMessage = this.nonDeliveryStep.execute(
                    message
            );
            // submit back NON_DELIVERY evidence message to the gateway
            this.linkSubmissionStep.execute(relayRemmdRejectionMessage);

            log.warn(
                    "Incoming message [{}] from gateway to backend has been rejected due to "
                    + "security exception",
                    message.identifier()
            );
        } finally {
            ConnectorBusinessDomainUtil.setCurrentBusinessDomain(null);
        }
    }
}
