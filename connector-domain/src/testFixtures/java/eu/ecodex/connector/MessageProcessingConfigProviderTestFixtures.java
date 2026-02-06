/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import eu.ecodex.connector.domain.model.property.ConnectorMessageProcessingProperties;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class MessageProcessingConfigProviderTestFixtures {
    public static ConnectorMessageProcessingProperties getProcessingProperties() {
        return ConnectorMessageProcessingProperties
                .builder()
                .identifierSuffix("domibus.connector.eu")
                .ebmsIdSuffix("ecodex.eu")
                .ebmsIdGeneratorEnabled(true)
                .sendGeneratedEvidencesToBackend(true)
                .inboundMessageVerificationMode(ProcessingModeVerificationMode.STRICT)
                .outboundMessageVerificationMode(ProcessingModeVerificationMode.RELAXED)
                .build();
    }
}
