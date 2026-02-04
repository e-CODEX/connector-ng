/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message.evidence;

import java.io.Serializable;
import lombok.Getter;

/**
 * An enumeration representing the various types of evidences related to messages in the connector
 * domain. Each evidence type is defined with a priority level, a positivity indication, and a
 * maximum occurrence count.
 *
 * <p>The evidence types include:
 * <ul>
 *     <li>SUBMISSION_ACCEPTANCE: Represents the acceptance of a submission.</li>
 *     <li>SUBMISSION_REJECTION: Represents the rejection of a submission.</li>
 *     <li>RELAY_REMMD_ACCEPTANCE: Represents the acceptance of a relay REMMD operation.</li>
 *     <li>RELAY_REMMD_REJECTION: Represents the rejection of a relay REMMD operation.</li>
 *     <li>RELAY_REMMD_FAILURE: Represents a failure in the relay REMMD operation.</li>
 *     <li>DELIVERY: Represents the successful delivery of a message.</li>
 *     <li>NON_DELIVERY: Represents the failure to deliver a message.</li>
 *     <li>RETRIEVAL: Represents the successful retrieval of a message.</li>
 *     <li>NON_RETRIEVAL: Represents the failure to retrieve a message.</li>
 * </ul>
 *
 * <p>Each evidence type is associated with the following attributes:
 * <ul>
 *     <li>priority: An integer indicating the priority of the evidence.</li>
 *     <li>
 *         positive: A boolean indicating whether the evidence is classified as positive or not.
 *     </li>
 *     <li>
 *         maxOccurrence: An integer defining the maximum number of times the evidence can occur.
 *     </li>
 * </ul>
 *
 * <p>A value of -1 indicates no limit on occurrences.
 *
 */
@Getter
public enum ConnectorEvidenceType implements Serializable {
    SUBMISSION_ACCEPTANCE(1, true, 1),
    SUBMISSION_REJECTION(2, false, 1),
    RELAY_REMMD_ACCEPTANCE(3, true, -1),
    RELAY_REMMD_REJECTION(5, false, -1),
    RELAY_REMMD_FAILURE(4, false, -1),
    DELIVERY(6, true, 1),
    NON_DELIVERY(7, false, 1),
    RETRIEVAL(8, true, 1),
    NON_RETRIEVAL(9, false, 1);

    private final int priority;
    private final boolean positive;
    private final int maxOccurrence;

    ConnectorEvidenceType(int priority, boolean positive, int maxOccurrence) {
        this.priority = priority;
        this.positive = positive;
        this.maxOccurrence = maxOccurrence;
    }
}
