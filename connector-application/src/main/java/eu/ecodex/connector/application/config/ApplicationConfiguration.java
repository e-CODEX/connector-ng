/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.config;

import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.application.service.message.inbound.pipeline.ConnectorInboundMessagePipeline;
import eu.ecodex.connector.application.service.message.outbound.pipeline.ConnectorOutboundMessagePipeline;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration class used for defining and managing application-level or framework-specific
 * configurations.
 */
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class ApplicationConfiguration {
    @Bean
    public ConnectorMessagePipeline connectorInboundMessagePipeline(
        @Qualifier("connectorInboundMessageBackendNameStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> backendNameStep,
        @Qualifier("connectorInboundMessageAcceptanceStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorEvidenceMessage> acceptanceStep,
        @Qualifier("connectorInboundMessageSecurityStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> securityStep,
        @Qualifier("connectorInboundMessageNonDeliveryStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorEvidenceMessage> nonDeliveryStep,
        @Qualifier("connectorMessageLinkSubmissionStep")
        ConnectorMessageStep<ConnectorMessage, ConnectorMessage> linkSubmissionStep) {
        return new ConnectorInboundMessagePipeline(
            backendNameStep,
            acceptanceStep,
            securityStep,
            nonDeliveryStep,
            linkSubmissionStep
        );
    }

    @Bean
    public ConnectorMessagePipeline connectorOutboundMessagePipeline(
        @Qualifier("connectorOutboundMessageValidationStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> validationStep,
        @Qualifier("connectorOutboundMessageSecurityStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> securityStep,
        @Qualifier("connectorOutboundMessageGatewayNameStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> gatewayNameStep,
        @Qualifier("connectorOutboundMessageEbmsIdStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> ebmsIdStep,
        @Qualifier("connectorOutboundMessageAcceptanceStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> acceptanceStep,
        @Qualifier("connectorOutboundMessageConfirmationStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorEvidenceMessage> confirmationStep,
        @Qualifier("connectorOutboundMessageRejectionStep")
        ConnectorMessageStep<ConnectorBusinessMessage, ConnectorEvidenceMessage> rejectionStep,
        @Qualifier("connectorMessageLinkSubmissionStep")
        ConnectorMessageStep<ConnectorMessage, ConnectorMessage> linkSubmissionStep
    ) {
        return new ConnectorOutboundMessagePipeline(
            validationStep,
            securityStep,
            gatewayNameStep,
            ebmsIdStep,
            acceptanceStep,
            confirmationStep,
            rejectionStep,
            linkSubmissionStep
        );
    }
}
