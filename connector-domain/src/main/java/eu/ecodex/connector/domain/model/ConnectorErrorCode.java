/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
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
 * Represents a set of error codes that can occur during the operation of a connector service. Each
 * error code is accompanied by a unique uuid and a description that provides additional
 * context about the specific error.
 *
 * <p>The error codes are categorized based on their context, such as issues related to
 * evidence processing and LinkPartner configurations. These codes can help in identifying and
 * troubleshooting specific problems within the connector system.
 */
@Getter
public enum ConnectorErrorCode {
    EVIDENCE_IGNORED_MESSAGE_ALREADY_REJECTED(
            "E101",
            "The processed evidence is ignored, because the business message is already "
            + "in rejected state"
    ),
    EVIDENCE_IGNORED_DUE_DUPLICATE(
            "E102",
            "The processed evidence is ignored, because max occurrence number of "
            + "evidence type exceeded"
    ),
    EVIDENCE_IGNORED_DUE_HIGHER_PRIORITY(
            "E103",
            "The processed evidence is not relevant due another evidence with higher "
            + "priority"
    ),
    LINK_PARTNER_NOT_FOUND("L104", "The requested LinkPartner is not configured"),
    LINK_PARTNER_NOT_ACTIVE("L101", "The requested LinkPartner is not active");

    private final String code;
    private final String description;

    ConnectorErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("{code=%s, description=%s}", code, description);
    }
}
