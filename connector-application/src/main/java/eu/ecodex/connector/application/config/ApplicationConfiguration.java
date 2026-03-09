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

import eu.ecodex.connector.application.service.impl.message.inbound.pipeline.ConnectorInboundMessagePipeline;
import eu.ecodex.connector.application.service.impl.message.outbound.pipeline.ConnectorOutboundMessagePipeline;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
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
            ConnectorMessageStep backendNameStep,
            @Qualifier("connectorInboundMessageAcceptanceStep")
            ConnectorMessageStep acceptanceStep,
            @Qualifier("connectorInboundMessageSecurityStep")
            ConnectorMessageStep securityStep,
            @Qualifier("connectorInboundMessageNonDeliveryStep")
            ConnectorMessageStep nonDeliveryStep,
            @Qualifier("connectorMessageLinkSubmissionStep")
            ConnectorMessageStep linkSubmissionStep) {
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
