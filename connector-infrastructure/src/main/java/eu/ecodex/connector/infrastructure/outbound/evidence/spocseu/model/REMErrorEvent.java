/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.evidence.spocseu.model;

import eu.ecodex.connector.infrastructure.outbound.evidence.spocseu.common.SpocsConstants.Evidences;
import jakarta.xml.soap.SOAPConstants;
import javax.xml.namespace.QName;
import lombok.Getter;

/**
 * An enumeration of possible REM error events.
 */
@SuppressWarnings("squid:S1135")
@Getter
public enum REMErrorEvent {
    SOAP_FAULT(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#TechnicalMalfunction",
        null,
        SOAPConstants.SOAP_RECEIVER_FAULT
    ),
    SAML_TOKEN_VALIDATION(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#PolicyViolation",
        null,
        SOAPConstants.SOAP_SENDER_FAULT
    ),
    WS_ADDRESSING_FAULT(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#PolicyViolation",
        "Invalid action URI",
        SOAPConstants.SOAP_SENDER_FAULT
    ),
    UNKNOWN_ORIGINATOR_ADDRESS(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#R_REMMD_NotIdentified",
        "Originator not known",
        SOAPConstants.SOAP_SENDER_FAULT
    ),
    UNKNOWN_RECIPIENT_ADDRESS(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#UnknownRecipient",
        "Recipient not known",
        SOAPConstants.SOAP_SENDER_FAULT
    ),
    UNSPECIFIC_PROCESSING_ERROR(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#TechnicalMalfunction",
        null,
        SOAPConstants.SOAP_RECEIVER_FAULT
    ),
    WRONG_INPUT_DATA(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#InvalidMessageFormat",
        null,
        SOAPConstants.SOAP_SENDER_FAULT
    ),
    DUPLICATE_MSG_ID(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.eu-spocs.eu/edelivery/v1#DuplicateMsgID",
        null,
        SOAPConstants.SOAP_SENDER_FAULT
    ),
    OTHER(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#Other",
        null,
        SOAPConstants.SOAP_RECEIVER_FAULT
    ),
    UNREACHABLE(
        Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
        "http:uri.etsi.org/REM/EventReason#R_REMMD_Unreachable",
        null,
        SOAPConstants.SOAP_SENDER_FAULT
    );

    private final Evidences evidence;
    private final String eventCode;
    private final String eventDetails;
    private final QName actor;

    REMErrorEvent(Evidences evidence, String eventCode, String eventDetails, QName actor) {
        this.evidence = evidence;
        this.eventCode = eventCode;
        this.eventDetails = eventDetails;
        this.actor = actor;
    }
}
