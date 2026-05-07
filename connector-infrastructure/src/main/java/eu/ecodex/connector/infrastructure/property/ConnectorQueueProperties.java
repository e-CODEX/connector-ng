/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the default connector queues.
 */
@Setter
@Getter
@Configuration
@SuppressWarnings("checkstyle:LineLength")
@ConfigurationProperties(prefix = "connector.queues")
public class ConnectorQueueProperties {
    private final String outboundMessageStagingQueue = "connector.queues.outbound-message-staging-queue";
    private final String outboundMessageProcessingQueue = "connector.queues.outbound-message-processing-queue";
    private final String inboundMessageProcessingQueue = "connector.queues.inbound-message-processing-queue";
    private final String gatewaySubmissionQueue = "domibus.backend.jms.inQueue";
    private final String gatewaySubmissionReplyQueue = "domibus.backend.jms.replyQueue";
}
