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
 * the connector. This record encapsulates the information needed to initiate the process.
 *
 * @param evidenceType the type of evidence being triggered, one of the values of
 *                     {@link ConnectorEvidenceType} (must not be null)
 * @param identifiers  the identifiers of the trigger message, used to correlate the request with
 *                     the related business message; see
 *                     {@link ConnectorEvidenceTriggerMessageIdentifiers} for their structure and
 *                     purpose (must not be null, and is validated recursively)
 */
@Builder(toBuilder = true)
public record ConnectorEvidenceTriggerMessageRequest(
    @NotNull(message = "The [evidenceType] must not be null")
    ConnectorEvidenceType evidenceType,

    @Valid
    @NotNull(message = "[Identifiers] must not be null")
    ConnectorEvidenceTriggerMessageIdentifiers identifiers
) {
}
