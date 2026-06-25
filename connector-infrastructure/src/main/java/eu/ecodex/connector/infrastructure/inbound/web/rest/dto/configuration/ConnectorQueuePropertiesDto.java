/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration;

import lombok.Builder;

/**
 * DTO for defining the queue configuration properties required by the connector.
 */
@Builder
public record ConnectorQueuePropertiesDto(
        String outboundMessageStagingQueue,
        String outboundEvidenceTriggerQueue,
        String outboundMessageProcessingQueue,
        String inboundMessageProcessingQueue,
        String inboundEvidenceTriggerQueue,
        String backendDeliveryQueue,
        String gatewaySubmissionQueue,
        String gatewaySubmissionReplyQueue
) {
}
