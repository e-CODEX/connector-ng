/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.property;

import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import lombok.Builder;

/**
 * Represents the configurable properties for processing messages within the connector. This record
 * defines various flags and parameters that influence the behaviour of the message processing
 * logic, including evidence handling, message ID generation, and PMode verification mode settings.
 *
 * @param sendGeneratedEvidencesToBackend Flag indicating whether generated evidences should be sent
 *                                        to the backend system.
 * @param ebmsIdGeneratorEnabled          Flag indicating whether the eBMS ID generation feature is
 *                                        enabled.
 * @param ebmsIdSuffix                    Suffix to be appended to generated eBMS IDs.
 * @param outboundMessageVerificationMode The verification mode to be applied to outgoing messages.
 * @param inboundMessageVerificationMode  The verification mode to be applied to incoming messages.
 */
@Builder(toBuilder = true)
public record ConnectorMessageProcessingProperties(
        boolean sendGeneratedEvidencesToBackend,
        boolean ebmsIdGeneratorEnabled,
        String identifierSuffix,
        String ebmsIdSuffix,
        ProcessingModeVerificationMode outboundMessageVerificationMode,
        ProcessingModeVerificationMode inboundMessageVerificationMode
) {
}
