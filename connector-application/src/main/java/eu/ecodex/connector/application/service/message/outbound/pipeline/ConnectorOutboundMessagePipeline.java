/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound.pipeline;

import eu.ecodex.connector.application.exception.ConnectorGatewaySubmissionException;
import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessageStep;
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
public class ConnectorOutboundMessagePipeline implements ConnectorMessagePipeline {
    private final ConnectorMessageStep validationStep;
    private final ConnectorMessageStep securityStep;
    private final ConnectorMessageStep gatewayValidationStep;
    private final ConnectorMessageStep ebmsIdStep;
    private final ConnectorMessageStep acceptanceStep;
    private final ConnectorMessageStep confirmationStep;
    private final ConnectorMessageStep rejectionStep;
    private final ConnectorMessageStep linkSubmissionStep;

    /**
     * Constructor for the ConnectorOutboundMessagePipeline class.
     *
     * @param validationStep        the validation step for outbound messages ensuring compliance
     *                              with required standards and formats must not be null.
     * @param securityStep          the security step for outbound messages handling tasks such as
     *                              encryption or signature validation, must not be null.
     * @param gatewayValidationStep the gateway validation step for outbound messages, responsible
     *                              for verifying gateway-specific requirements, must not be null.
     * @param ebmsIdStep            the EBMS-ID step for outbound messages,
     * @param acceptanceStep        the submission acceptance step for outbound messages,
     *                              responsible for approving the message for delivery, must not be
     *                              null.
     * @param confirmationStep      the confirmation step for outbound messages handling the process
     *                              of confirming successful message delivery; must not be null.
     * @param rejectionStep         the rejection step for outbound messages, addressing cases where
     *                              the message is not accepted; must not be null.
     * @param linkSubmissionStep    the service responsible for submitting the processed message to
     *                              the appropriate endpoint or next stage; must not be null.
     */
    public ConnectorOutboundMessagePipeline(
        ConnectorMessageStep validationStep,
        ConnectorMessageStep securityStep,
        ConnectorMessageStep gatewayValidationStep,
        ConnectorMessageStep ebmsIdStep,
        ConnectorMessageStep acceptanceStep,
        ConnectorMessageStep confirmationStep,
        ConnectorMessageStep rejectionStep,
        ConnectorMessageStep linkSubmissionStep) {
        this.validationStep = validationStep;
        this.securityStep = securityStep;
        this.gatewayValidationStep =
            gatewayValidationStep;
        this.ebmsIdStep = ebmsIdStep;
        this.acceptanceStep =
            acceptanceStep;
        this.confirmationStep = confirmationStep;
        this.rejectionStep = rejectionStep;
        this.linkSubmissionStep = linkSubmissionStep;
    }

    @Override
    public void process(@NonNull ConnectorMessage message) {
        log.info("Start processing outbound connector message: [{}]", message.identifier());

        try {
            ConnectorBusinessDomainUtil.setCurrentBusinessDomain(
                message.businessDomainIdentifier()
            );
            var outboundMessage = this.validationStep.execute(message);
            // validate the ASIC-S container
            outboundMessage = this.securityStep.execute(outboundMessage);
            outboundMessage = this.gatewayValidationStep.execute(outboundMessage);
            outboundMessage = this.ebmsIdStep.execute(outboundMessage);

            outboundMessage = this.acceptanceStep.execute(outboundMessage);

            // submit the message to the gateway
            this.linkSubmissionStep.execute(outboundMessage);

            // submit SUBMISSION_ACCEPTANCE evidence message back to the backend
            var confirmationMessage = this.confirmationStep.execute(outboundMessage);

            this.linkSubmissionStep.execute(confirmationMessage);

            log.info("End processing outbound connector message: [{}]", message.identifier());
        } catch (Exception e) {
            log.error(
                "Evidence [{}] generation for message [{}] failed",
                ConnectorEvidenceType.SUBMISSION_REJECTION, message.identifier(), e
            );

            var rejectionMessage = this.rejectionStep.execute(message);
            this.linkSubmissionStep.execute(rejectionMessage);

            throw new ConnectorGatewaySubmissionException(
                "Message submission to gateway failed", e
            );
        } finally {
            ConnectorBusinessDomainUtil.setCurrentBusinessDomain(null);
        }
    }
}
