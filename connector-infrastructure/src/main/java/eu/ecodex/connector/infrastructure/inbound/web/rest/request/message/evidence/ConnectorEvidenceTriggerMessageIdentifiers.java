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

import eu.ecodex.connector.infrastructure.inbound.web.rest.validator.constraints.AtLeastOneNotEmpty;
import lombok.Builder;

/**
 * Represents a set of identifiers associated with a trigger message for evidence-related operations
 * in the connector domain. This record encapsulates the unique identifiers used to correlate the
 * business messages.
 *
 * @param backendMessageIdentifier A unique identifier for the message in the backend system,
 *                                 providing traceability within internal processes.
 * @param referenceToIdentifier    A reference to another message or entity, aiding in the
 *                                 establishment of relationships or dependencies between different
 *                                 messages.
 */
@AtLeastOneNotEmpty(fields = {
        "backendMessageIdentifier",
        "referenceToIdentifier"
})
@Builder
public record ConnectorEvidenceTriggerMessageIdentifiers(
        String backendMessageIdentifier,
        String referenceToIdentifier
) {
}
