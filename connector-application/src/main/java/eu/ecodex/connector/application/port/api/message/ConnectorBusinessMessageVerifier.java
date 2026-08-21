/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message;

import eu.ecodex.connector.application.exception.ConnectorProcessingModeVerificationException;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;

/**
 * Defines a contract for verifying {@link ConnectorBusinessMessage} instances according to a
 * specific {@link ProcessingModeVerificationMode}.
 *
 * <p>
 * Implementations are responsible for validating that a message complies with the required
 * processing mode rules, business constraints, and structural requirements before further
 * processing.
 * </p>
 *
 * <p>
 * Verification may include checks such as:
 * <ul>
 *     <li>Service and action consistency</li>
 *     <li>Party constraints</li>
 * </ul>
 * </p>
 */
public interface ConnectorBusinessMessageVerifier {
    /**
     * Verifies the given {@link ConnectorMessage} according to the provided
     * {@link ProcessingModeVerificationMode}.
     *
     * @param message          the message to verify; must not be {@code null}
     * @param verificationMode defines the strictness or type of verification to apply; must not be
     *                         {@code null}
     *
     * @throws ConnectorProcessingModeVerificationException if verification fails.
     */
    void verify(
        @NonNull ConnectorMessage message,
        @NonNull ProcessingModeVerificationMode verificationMode);
}
