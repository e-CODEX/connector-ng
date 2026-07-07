/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.dto;

import lombok.Builder;

/**
 * Data transfer object containing aggregated message statistics for a connector.
 *
 * @param total                     the total number of messages
 * @param delivered                 the total number of successfully delivered messages
 * @param rejected                  the total number of rejected messages
 * @param backendToGateway          the total number of messages sent from the backend to the
 *                                  gateway
 * @param backendToGatewayDelivered the number of backend-to-gateway messages that were successfully
 *                                  delivered
 * @param backendToGatewayRejected  the number of backend-to-gateway messages that were rejected
 * @param gatewayToBackend          the total number of messages sent from the gateway to the
 *                                  backend
 * @param gatewayToBackendDelivered the number of gateway-to-backend messages that were successfully
 *                                  delivered
 * @param gatewayToBackendRejected  the number of gateway-to-backend messages that were rejected
 */
@Builder
public record ConnectorMessageStatsDto(
        long total,
        long delivered,
        long rejected,
        long backendToGateway,
        long backendToGatewayDelivered,
        long backendToGatewayRejected,
        long gatewayToBackend,
        long gatewayToBackendDelivered,
        long gatewayToBackendRejected
) {
}
