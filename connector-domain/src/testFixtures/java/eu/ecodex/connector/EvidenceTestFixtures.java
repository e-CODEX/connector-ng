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

import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidence;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class EvidenceTestFixtures {
    public static ConnectorEvidence createSubmissionAcceptanceEvidence() {
        return ConnectorEvidence.builder()
                                .type(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE)
                                .content(new byte[1])
                                .build();
    }

    public static ConnectorEvidence createSubmissionRejectionEvidence() {
        return ConnectorEvidence.builder()
                                .type(ConnectorEvidenceType.SUBMISSION_REJECTION)
                                .content(new byte[1])
                                .build();
    }

    public static ConnectorEvidence createRelayREMMDAcceptanceEvidence() {
        return ConnectorEvidence.builder()
                                .type(ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE)
                                .content(new byte[1])
                                .build();
    }

    public static ConnectorEvidence createRelayREMMDRejectionEvidence() {
        return ConnectorEvidence.builder()
                                .type(ConnectorEvidenceType.RELAY_REMMD_REJECTION)
                                .content(new byte[1])
                                .build();
    }

    public static ConnectorEvidence createRelayREMMDFailureEvidence() {
        return ConnectorEvidence.builder()
                                .type(ConnectorEvidenceType.RELAY_REMMD_FAILURE)
                                .content(new byte[1])
                                .build();
    }

    public static ConnectorEvidence createDeliveryEvidence() {
        return ConnectorEvidence.builder()
                                .type(ConnectorEvidenceType.DELIVERY)
                                .content(new byte[1])
                                .build();
    }

    public static ConnectorEvidence createNonDeliveryEvidence() {
        return ConnectorEvidence.builder()
                                .type(ConnectorEvidenceType.NON_DELIVERY)
                                .content(new byte[1])
                                .build();
    }

    public static ConnectorEvidence createRetrievalEvidence() {
        return ConnectorEvidence.builder()
                                .type(ConnectorEvidenceType.RETRIEVAL)
                                .content(new byte[1])
                                .build();
    }

    public static ConnectorEvidence createNonRetrievalEvidence() {
        return ConnectorEvidence.builder()
                                .type(ConnectorEvidenceType.NON_RETRIEVAL)
                                .content(new byte[1])
                                .build();
    }

    public static ConnectorEvidence createEvidenceTrigger() {
        return ConnectorEvidence.builder()
                                .type(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE)
                                .content(new byte[0])
                                .build();
    }
}
