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

import eu.ecodex.connector.application.service.impl.message.outbound.pipeline.ConnectorOutboundMessagePipeline;
import eu.ecodex.connector.domain.api.link.ConnectorLinkSubmissionService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.service.pipeline.inbound.ConnectorInboundMessagePipeline;
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
    public ConnectorMessagePipeline connectorInboundMessagePipeline(
            @Qualifier("connectorInboundMessageValidationStep")
            ConnectorMessageStep connectorInboundMessageValidationStep,
            @Qualifier("connectorInboundMessageBackendValidationStep")
            ConnectorMessageStep connectorInboundMessageBackendValidationStep,
            @Qualifier("connectorInboundMessageAcceptanceStep")
            ConnectorMessageStep connectorInboundMessageAcceptanceStep,
            @Qualifier("connectorInboundMessageSecurityStep")
            ConnectorMessageStep connectorInboundMessageSecurityStep,
            @Qualifier("connectorInboundMessageNonDeliveryStep")
            ConnectorMessageStep connectorInboundMessageNonDeliveryStep,
            @Qualifier("connectorLinkSubmissionService")
            ConnectorLinkSubmissionService connectorLinkSubmissionService) {
        return new ConnectorInboundMessagePipeline(
                connectorInboundMessageValidationStep,
                connectorInboundMessageBackendValidationStep,
                connectorInboundMessageAcceptanceStep,
                connectorInboundMessageSecurityStep,
                connectorInboundMessageNonDeliveryStep,
                connectorLinkSubmissionService
        );
    }

    @Bean
    public ConnectorMessagePipeline connectorOutboundMessagePipeline(
            @Qualifier("connectorOutboundMessageValidationStep")
            ConnectorMessageStep validationStep,
            @Qualifier("connectorOutboundMessageSecurityStep")
            ConnectorMessageStep securityStep,
            @Qualifier("connectorOutboundMessageGatewayNameStep")
            ConnectorMessageStep gatewayNameStep,
            @Qualifier("connectorOutboundMessageEbmsIdStep")
            ConnectorMessageStep ebmsIdStep,
            @Qualifier("connectorOutboundMessageAcceptanceStep")
            ConnectorMessageStep acceptanceStep,
            @Qualifier("connectorOutboundMessageConfirmationStep")
            ConnectorMessageStep confirmationStep,
            @Qualifier("connectorOutboundMessageRejectionStep")
            ConnectorMessageStep rejectionStep,
            @Qualifier("connectorMessageLinkSubmissionStep")
            ConnectorMessageStep linkSubmissionStep
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
