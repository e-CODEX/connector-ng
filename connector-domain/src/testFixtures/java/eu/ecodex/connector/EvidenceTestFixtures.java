/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "checkstyle:LineLength"})
public class EvidenceTestFixtures {
    public static ConnectorMessageEvidence createSubmissionAcceptanceEvidence() {
        return ConnectorMessageEvidence.builder()
                                       .uuid("12345678-1234-1234-1234-123456789012")
                                       .type(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE)
                                       .content(new byte[]{1, 2, 3})
                                       .build();
    }

    public static ConnectorMessageEvidence createSubmissionRejectionEvidence() {
        return ConnectorMessageEvidence.builder()
                                       .type(ConnectorEvidenceType.SUBMISSION_REJECTION)
                                       .content(new byte[]{1, 2, 3})
                                       .build();
    }

    public static ConnectorMessageEvidence createRelayREMMDAcceptanceEvidence() {
        return ConnectorMessageEvidence.builder()
                                       .type(ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE)
                                       .content(new byte[]{1, 2, 3})
                                       .build();
    }

    public static ConnectorMessageEvidence createRelayREMMDRejectionEvidence() {
        return ConnectorMessageEvidence.builder()
                                       .type(ConnectorEvidenceType.RELAY_REMMD_REJECTION)
                                       .content(new byte[]{1, 2, 3})
                                       .build();
    }

    public static ConnectorMessageEvidence createRelayREMMDFailureEvidence() {
        return ConnectorMessageEvidence.builder()
                                       .type(ConnectorEvidenceType.RELAY_REMMD_FAILURE)
                                       .content(new byte[]{1, 2, 3})
                                       .build();
    }

    public static ConnectorMessageEvidence createDeliveryEvidence() {
        return ConnectorMessageEvidence.builder()
                                       .uuid("12345678-1234-1234-1234-123456789012")
                                       .type(ConnectorEvidenceType.DELIVERY)
                                       .content(new byte[]{1, 2, 3})
                                       .build();
    }

    public static ConnectorMessageEvidence createNonDeliveryEvidence() {
        return ConnectorMessageEvidence.builder()
                                       .type(ConnectorEvidenceType.NON_DELIVERY)
                                       .content(new byte[]{1, 2, 3})
                                       .build();
    }

    public static ConnectorMessageEvidence createRetrievalEvidence() {
        return ConnectorMessageEvidence.builder()
                                       .type(ConnectorEvidenceType.RETRIEVAL)
                                       .content(new byte[]{1, 2, 3})
                                       .build();
    }

    public static ConnectorMessageEvidence createNonRetrievalEvidence() {
        return ConnectorMessageEvidence.builder()
                                       .type(ConnectorEvidenceType.NON_RETRIEVAL)
                                       .content(new byte[]{1, 2, 3})
                                       .build();
    }

    public static ConnectorMessageEvidence createEvidenceTrigger() {
        return ConnectorMessageEvidence.builder()
                                       .type(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE)
                                       .content(null)
                                       .build();
    }
}
