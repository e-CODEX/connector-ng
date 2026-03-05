/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.message;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Defines the contract for processing outbound {@link ConnectorMessage} instances.
 *
 * <p>Implementations are responsible for preparing a message before it is
 * dispatched to an external party. Processing may include tasks such as:
 * <ul>
 *     <li>Enrichment of message metadata</li>
 *     <li>Validation and verification</li>
 *     <li>Transformation or normalization</li>
 * </ul>
 * </p>
 *
 * <p>The processor may modify and return the same instance or return a new,
 * processed {@link ConnectorMessage} instance depending on the implementation.
 */
public interface ConnectorOutboundMessageProcessor {
    ConnectorMessage process(@Nonnull ConnectorMessage message);
}
