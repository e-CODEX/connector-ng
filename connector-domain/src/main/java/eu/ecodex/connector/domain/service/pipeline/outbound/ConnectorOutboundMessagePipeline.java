/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.outbound;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.link.ConnectorLinkSubmissionService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.exception.ConnectorGatewaySubmissionException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.util.ConnectorBusinessDomainUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes outbound connector messages within the system.
 *
 * <p>This class plays a key role in handling messages that are intended to be sent externally. It
 * is part of the outbound message processing pipeline, supporting the application domain by
 * implementing the {@link ConnectorMessagePipeline} interface.
 *
 * <p>Thread-Safety: While this class does not maintain an internal state, coordination for message
 * processing is required in multithreaded environments to handle thread-specific message attributes
 * if applicable.
 */
@Slf4j
@DomainService
public class ConnectorOutboundMessagePipeline implements ConnectorMessagePipeline {
    // IMPORTANT: The names of the steps are important! They should correspond to the names of
    //  the implementation classes, but in camel case.
    private final ConnectorMessageStep connectorOutboundMessageValidationStep;
    private final ConnectorMessageStep connectorOutboundMessageSecurityStep;
    private final ConnectorMessageStep connectorOutboundMessageGatewayValidationStep;
    private final ConnectorMessageStep connectorOutboundMessageEbmsIdStep;
    private final ConnectorMessageStep connectorOutboundMessageSubmissionAcceptanceStep;
    private final ConnectorMessageStep connectorOutboundMessageConfirmationStep;
    private final ConnectorMessageStep connectorOutboundMessageRejectionStep;
    private final ConnectorLinkSubmissionService linkSubmissionService;

    /**
     * Constructor for the ConnectorOutboundMessagePipeline class.
     *
     * @param connectorOutboundMessageValidationStep           the validation step for outbound
     *                                                         messages ensuring compliance with
     *                                                         required standards and formats must
     *                                                         not be null.
     * @param connectorOutboundMessageSecurityStep             the security step for outbound
     *                                                         messages handling tasks such as
     *                                                         encryption or signature validation,
     *                                                         must not be null.
     * @param connectorOutboundMessageGatewayValidationStep    the gateway validation step for
     *                                                         outbound messages, responsible for
     *                                                         verifying gateway-specific
     *                                                         requirements, must not be null.
     * @param connectorOutboundMessageEbmsIdStep               the EBMS-ID step for outbound
     *                                                         messages,
     * @param connectorOutboundMessageSubmissionAcceptanceStep the submission acceptance step for
     *                                                         outbound messages, responsible for
     *                                                         approving the message for delivery,
     *                                                         must not be null.
     * @param connectorOutboundMessageConfirmationStep         the confirmation step for outbound
     *                                                         messages handling the process of
     *                                                         confirming successful message
     *                                                         delivery; must not be null.
     * @param connectorOutboundMessageRejectionStep            the rejection step for outbound
     *                                                         messages, addressing cases where the
     *                                                         message is not accepted; must not be
     *                                                         null.
     * @param linkSubmissionService                            the service responsible for
     *                                                         submitting the processed message to
     *                                                         the appropriate endpoint or next
     *                                                         stage; must not be null.
     */
    public ConnectorOutboundMessagePipeline(
            ConnectorMessageStep connectorOutboundMessageValidationStep,
            ConnectorMessageStep connectorOutboundMessageSecurityStep,
            ConnectorMessageStep connectorOutboundMessageGatewayValidationStep,
            ConnectorMessageStep connectorOutboundMessageEbmsIdStep,
            ConnectorMessageStep connectorOutboundMessageSubmissionAcceptanceStep,
            ConnectorMessageStep connectorOutboundMessageConfirmationStep,
            ConnectorMessageStep connectorOutboundMessageRejectionStep,
            ConnectorLinkSubmissionService linkSubmissionService) {
        this.connectorOutboundMessageValidationStep = connectorOutboundMessageValidationStep;
        this.connectorOutboundMessageSecurityStep = connectorOutboundMessageSecurityStep;
        this.connectorOutboundMessageGatewayValidationStep =
                connectorOutboundMessageGatewayValidationStep;
        this.connectorOutboundMessageEbmsIdStep = connectorOutboundMessageEbmsIdStep;
        this.connectorOutboundMessageSubmissionAcceptanceStep =
                connectorOutboundMessageSubmissionAcceptanceStep;
        this.connectorOutboundMessageConfirmationStep = connectorOutboundMessageConfirmationStep;
        this.connectorOutboundMessageRejectionStep = connectorOutboundMessageRejectionStep;
        this.linkSubmissionService = linkSubmissionService;
    }

    @Override
    public void process(@NonNull ConnectorMessage message) {
        log.info("start processing outbound connector message: [{}]", message);

        try {
            ConnectorBusinessDomainUtil.setCurrentBusinessDomain(
                    message.businessDomainIdentifier()
            );
            var outboundMessage = this.connectorOutboundMessageValidationStep.execute(message);
            // validate the ASIC-S container
            outboundMessage = this.connectorOutboundMessageSecurityStep.execute(outboundMessage);
            outboundMessage = this.connectorOutboundMessageGatewayValidationStep.execute(
                    outboundMessage
            );
            outboundMessage = this.connectorOutboundMessageEbmsIdStep.execute(outboundMessage);
            outboundMessage = this.connectorOutboundMessageSubmissionAcceptanceStep.execute(
                    outboundMessage
            );
            // submit the message to the gateway
            this.linkSubmissionService.submit(outboundMessage);
            outboundMessage = this.connectorOutboundMessageConfirmationStep.execute(
                    outboundMessage
            );
            // submit SUBMISSION_ACCEPTANCE evidence message back to the backend
            this.linkSubmissionService.submit(outboundMessage);

            log.info("end processing outbound connector message: [{}]", message);
        } catch (Exception e) {
            log.error(
                    "evidence [{}] generation for message [{}] failed",
                    ConnectorEvidenceType.SUBMISSION_REJECTION, message.identifier(), e
            );

            var rejectionMessage = this.connectorOutboundMessageRejectionStep.execute(message);
            this.linkSubmissionService.submit(rejectionMessage);

            throw new ConnectorGatewaySubmissionException(
                    "message submission to gateway failed", e
            );
        } finally {
            ConnectorBusinessDomainUtil.setCurrentBusinessDomain(null);
        }
    }
}
