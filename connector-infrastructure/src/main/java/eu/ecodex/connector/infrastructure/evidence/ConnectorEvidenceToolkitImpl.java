/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence;

import eu.ecodex.connector.domain.api.ConnectorEvidenceToolkit;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidence;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorEvidenceToolkit}.
 */
@Component
public class ConnectorEvidenceToolkitImpl implements ConnectorEvidenceToolkit {
    @Override
    public ConnectorEvidence create(
            @NonNull ConnectorMessage message,
            @NonNull ConnectorEvidenceType evidenceType,
            @Nullable ConnectorMessageRejectionReason rejectionReason) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
