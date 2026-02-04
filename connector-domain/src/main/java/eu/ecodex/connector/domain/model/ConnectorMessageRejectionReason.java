/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model;

import lombok.Getter;

/**
 * Represents the possible reasons for rejecting a message in the connector system.
 *
 * <p>This enum is used to classify and describe different rejection scenarios that may occur
 * during the processing of messages in the system. Each rejection reason is associated with an
 * error code and a descriptive reason message, providing clear information about why a message was
 * rejected.
 *
 * <p>The reasons defined include:
 * <ul>
 *     <li> General errors during message processing.
 *     <li> Backend service or application-specific rejections.
 *     <li> Gateway service or application-specific rejections.
 *     <li> Timeout events for evidence delivery or relay acceptance/rejection.
 *     <li> Scenarios where the target system is unreachable.
 * </ul>
 */
@Getter
public enum ConnectorMessageRejectionReason {
    //    SOAP_FAULT("E100", ""),
    //    SAML_TOKEN_VALIDATION("E101", ""),
    //    WS_ADDRESSING_FAULT(""),
    //    UNKNOWN_ORIGINATOR_ADDRESS(""),
    //    UNKNOWN_RECIPIENT_ADDRESS(""),
    UNSPECIFIC_PROCESSING_ERROR("E100", ""),
    //    WRONG_INPUT_DATA(""),
    //    DUPLICATE_MSG_ID(""),
    OTHER("E100", ""),
    BACKEND_REJECTION(
            "E200",
            "The connector backend or backend application rejected the message"
    ),
    GW_REJECTION("E201", "The gateway or gateway application rejected the message"),
    RELAY_REMMD_TIMEOUT(
            "E301",
            "The maximum wait time for a RELAY_REMMD_ACCEPTANCE/RELAY_REMMD_REJECTION "
            + "evidence has been reached!"
    ),
    DELIVERY_EVIDENCE_TIMEOUT(
            "E300",
            "The maximum wait time for a NON_DELIVERY/DELIVERY evidence has been reached!"
    ),
    UNREACHABLE("E404", "");

    private final String reason;
    private final String errorCode;

    ConnectorMessageRejectionReason(String errorCode, String reason) {
        this.errorCode = errorCode;
        this.reason = reason;
    }
}
