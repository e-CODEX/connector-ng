/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom; Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL; Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration;

import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import lombok.Builder;

/**
 * DTO for defining the message processing properties used in the connector. This record
 * consolidates various configuration options related to message identifiers, transport identifiers,
 * processing modes, and evidence handling.
 *
 * @param ebmsIdGeneratorEnabled          Specifies whether the ebMS ID generator is enabled.
 * @param identifierSuffix                A suffix to append to message identifiers for uniqueness.
 * @param ebmsIdSuffix                    A suffix to apply specifically to ebMS IDs.
 * @param transportIdSuffix               A suffix to apply to transport-related identifiers.
 * @param outboundMessageVerificationMode Specifies the verification mode for processing outbound
 *                                        messages.
 * @param inboundMessageVerificationMode  Specifies the verification mode for processing inbound
 *                                        messages.
 * @param sendGeneratedEvidencesToBackend Indicates whether generated evidences should be sent back
 *                                        to the backend.
 * @param autoTriggerDeliveryEvidences    Flag indicating whether automatic evidence delivery should
 *                                        be enabled.
 */
@Builder
public record ConnectorMessageProcessingPropertiesDto(
    boolean ebmsIdGeneratorEnabled,
    String identifierSuffix,
    String ebmsIdSuffix,
    String transportIdSuffix,
    ProcessingModeVerificationMode outboundMessageVerificationMode,
    ProcessingModeVerificationMode inboundMessageVerificationMode,
    boolean sendGeneratedEvidencesToBackend,
    boolean autoTriggerDeliveryEvidences
) {
}
