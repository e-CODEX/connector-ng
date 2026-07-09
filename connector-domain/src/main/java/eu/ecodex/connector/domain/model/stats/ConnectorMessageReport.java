/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.stats;

import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import lombok.Builder;

/**
 * Represents a report containing aggregated statistical data about connector messages. This record
 * holds information such as the year, month, involved party, service, message direction, and total
 * count of messages.
 *
 * @param year      The calendar year associated with the report.
 * @param month     The calendar month associated with the report.
 * @param party     The party associated with the messages, typically identifying the organization
 *                  involved in the message exchange.
 * @param service   The service through which the messages were processed.
 * @param direction The direction of the message flow, indicating whether messages originate from
 *                  the backend and are directed to the gateway or vice versa.
 * @param total     The total number of messages processed for the given parameters.
 */
@Builder
public record ConnectorMessageReport(
        int year,
        int month,
        String party,
        String service,
        ConnectorMessageDirection direction,
        long total
) {
}
