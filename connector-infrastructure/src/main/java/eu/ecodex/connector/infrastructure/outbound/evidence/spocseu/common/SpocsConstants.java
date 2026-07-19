/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.evidence.spocseu.common;

import lombok.Getter;

/**
 * This class contains constant values used in the Spocs application.
 */
@SuppressWarnings("checkstyle:TypeName")
public class SpocsConstants {
    /**
     * Evidences are an enum that represents different types of evidence in a system. Each evidence
     * type has a name, a fault event code, and a success event code. It also provides a method to
     * retrieve an evidence element based on its name.
     */
    @Getter
    public enum Evidences {
        SUBMISSION_ACCEPTANCE_REJECTION(
            "SubmissionAcceptanceRejection",
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_ACCEPTANCE,
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_REJECTION
        ),
        RELAY_REM_MD_ACCEPTANCE_REJECTION(
            "RelayREMMDAcceptanceRejection",
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_ACCEPTANCE,
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_REJECTION
        ),
        RELAY_REM_MD_FAILURE(
            "RelayREMMDFailure",
            "",
            "http:uri.etsi.org/02640/Event#DeliveryExpiration"
        ),
        DELIVERY_NON_DELIVERY_TO_RECIPIENT(
            "DeliveryNonDeliveryToRecipient",
            "http:uri.etsi.org/02640/Event#Delivery",
            "http:uri.etsi.org/02640/Event#NonDelivery"
        ),
        RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT(
            "RetrievalNonRetrievalByRecipient",
            "http:uri.etsi.org/02640/Event#Retrieval",
            "http:uri.etsi.org/02640/Event#RetrievalExpiration"
        ),
        ACCEPTANCE_REJECTION_BY_RECIPIENT(
            "AcceptanceRejectionByRecipient",
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_ACCEPTANCE,
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_REJECTION
        ),
        RECEIVED_BY_NON_REM_SYSTEM(
            "ReceivedByNonREMSystem",
            "",
            ""
        );
        private final String name;
        private final String faultEventCode;
        private final String successEventCode;

        Evidences(String name, String successEventCode, String faultEventCode) {
            this.name = name;
            this.faultEventCode = faultEventCode;
            this.successEventCode = successEventCode;
        }

        private static class Constants {
            public static final String HTTP_URI_ETSI_ORG_02640_EVENT_ACCEPTANCE =
                "http:uri.etsi.org/02640/Event#Acceptance";
            public static final String HTTP_URI_ETSI_ORG_02640_EVENT_REJECTION =
                "http:uri.etsi.org/02640/Event#Rejection";
        }
    }
}
