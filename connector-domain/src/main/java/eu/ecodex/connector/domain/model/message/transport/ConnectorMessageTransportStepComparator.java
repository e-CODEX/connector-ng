/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message.transport;

import java.time.Instant;
import java.util.Comparator;

/**
 * Compares instances of {@code ConnectorMessageTransportStepStatus} based on their creation
 * timestamps and the priority of their transport statuses.
 *
 * <p>This comparator is designed to sort transport step statuses first by their creation
 * timestamps in descending order (most recent first). If two statuses have identical creation
 * timestamps, they are further compared based on the priority values of their transport statuses,
 * with lower priority values indicating higher importance.
 *
 * <p>The comparison logic is as follows:
 * <ul>
 *     <li>If both statuses have non-null {@code createdAt} timestamps, the most recent timestamp is
 *         considered higher in order.</li>
 *     <li>If timestamp comparison results in equality, the {@code status} of each transport step is
 *         compared based on its assigned priority.</li>
 *     <li>If any {@code createdAt} or {@code status} fields are {@code null}, default values are
 *         used ({@code Instant.MIN} for timestamps and {@code PENDING} for statuses).</li>
 * </ul>
 *
 * <p>This comparator is useful in scenarios where transport steps must be evaluated for queue
 * processing or prioritized operations in the connector system.
 */
public class ConnectorMessageTransportStepComparator implements
        Comparator<ConnectorMessageTransportStepStatus> {
    @Override
    public int compare(
            ConnectorMessageTransportStepStatus stepStatus1,
            ConnectorMessageTransportStepStatus stepStatus2) {

        var creationTime1 = Instant.MIN;
        if (stepStatus1.createdAt() != null) {
            creationTime1 = stepStatus1.createdAt();
        }

        var creationTime2 = Instant.MIN;
        if (stepStatus2.createdAt() != null) {
            creationTime2 = stepStatus2.createdAt();
        }

        int comparison = creationTime2.compareTo(creationTime1);
        if (comparison != 0) {
            return comparison;
        }

        var status1 = ConnectorMessageTransportStatus.PENDING;
        var status2 = ConnectorMessageTransportStatus.PENDING;

        if (stepStatus1.status() != null) {
            status1 = stepStatus1.status();
        }
        if (stepStatus2.status() != null) {
            status2 = stepStatus2.status();
        }

        return status1.getPriority() - status2.getPriority();
    }
}
