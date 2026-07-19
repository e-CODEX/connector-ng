/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.evidence;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Represents a request to trigger an evidence-related operation for a specific business message in
 * the connector. This record encapsulates the relevant information needed to initiate the process.
 *
 * @param evidenceType The type of evidence being triggered. It corresponds to a predefined set of
 *                     evidence types in the {@link ConnectorEvidenceType} enumeration.
 * @param identifiers  A set of identifiers associated with the trigger message. These identifiers
 *                     provide a correlation for the related business message. Refer to
 *                     {@link ConnectorEvidenceTriggerMessageIdentifiers} for details on the
 *                     structure and purpose of these identifiers.
 */
@Builder(toBuilder = true)
public record ConnectorEvidenceTriggerMessageRequest(
    @NotNull(message = "Evidence type must not be null")
    ConnectorEvidenceType evidenceType,
    @Valid
    @NotNull(message = "Identifiers must not be null")
    ConnectorEvidenceTriggerMessageIdentifiers identifiers
) {
}
