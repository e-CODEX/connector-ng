/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.outbound;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import jakarta.annotation.Nonnull;
import lombok.Builder;

/**
 * This record represents a command for outbound evidence message processing. It encapsulates
 * details about the type of evidence, the backend message identifier, the reference to identifier,
 * and the backend name.
 *
 * @param evidenceType          the type of evidence
 * @param referenceToIdentifier the identifier to which the evidence is referenced (ebms ID or
 *                              backend ID or message ID)
 * @param backendName           the name of the backend
 */
@Builder
public record ConnectorOutboundEvidenceMessageCommand(
    @Nonnull ConnectorEvidenceType evidenceType,
    String referenceToIdentifier,
    @Nonnull String backendName
) {
}
