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

import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import lombok.Builder;

/**
 * Represents an aggregated connector message report for a specific year, month, party, service, and
 * message direction.
 *
 * @param year      the calendar year
 * @param month     the month of the year, from {@code 1} (January) to {@code 12} (December)
 * @param party     the party associated with the messages
 * @param service   the service associated with the messages
 * @param direction the direction of the messages
 * @param total     the total number of messages matching the specified criteria
 */
@Builder
public record ConnectorMessageReportDto(
    int year,
    int month,
    String party,
    String service,
    ConnectorMessageDirection direction,
    long total
) {
}
