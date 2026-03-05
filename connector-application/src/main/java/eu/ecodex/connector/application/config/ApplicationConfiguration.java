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

import eu.ecodex.connector.domain.api.link.ConnectorLinkSubmissionService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.service.pipeline.inbound.ConnectorInboundMessagePipeline;
import eu.ecodex.connector.domain.service.pipeline.outbound.ConnectorOutboundMessagePipeline;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public ConnectorMessagePipeline connectorOutboundMessagePipeline(
            @Qualifier("connectorOutboundMessageValidationStep")
            ConnectorMessageStep connectorOutboundMessageValidationStep,
            @Qualifier("connectorOutboundMessageSecurityStep")
            ConnectorMessageStep connectorOutboundMessageSecurityStep,
            @Qualifier("connectorOutboundMessageGatewayValidationStep")
            ConnectorMessageStep connectorOutboundMessageGatewayValidationStep,
            @Qualifier("connectorOutboundMessageEbmsIdStep")
            ConnectorMessageStep connectorOutboundMessageEbmsIdStep,
            @Qualifier("connectorOutboundMessageSubmissionAcceptanceStep")
            ConnectorMessageStep connectorOutboundMessageSubmissionAcceptanceStep,
            @Qualifier("connectorOutboundMessageConfirmationStep")
            ConnectorMessageStep connectorOutboundMessageConfirmationStep,
            @Qualifier("connectorOutboundMessageRejectionStep")
            ConnectorMessageStep connectorOutboundMessageRejectionStep,
            @Qualifier("connectorLinkSubmissionService")
            ConnectorLinkSubmissionService linkSubmissionService
    ) {
        return new ConnectorOutboundMessagePipeline(
                connectorOutboundMessageValidationStep,
                connectorOutboundMessageSecurityStep,
                connectorOutboundMessageGatewayValidationStep,
                connectorOutboundMessageEbmsIdStep,
                connectorOutboundMessageSubmissionAcceptanceStep,
                connectorOutboundMessageConfirmationStep,
                connectorOutboundMessageRejectionStep,
                linkSubmissionService
        );
    }
}
