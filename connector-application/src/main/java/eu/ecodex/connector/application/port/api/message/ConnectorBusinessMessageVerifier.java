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

import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;

/**
 * Defines a contract for verifying {@link ConnectorMessage} instances according to a specific
 * {@link ProcessingModeVerificationMode}.
 *
 * <p>Implementations are responsible for validating that a message complies with the required
 * processing mode rules, business constraints, and structural requirements before further
 * processing.
 *
 * <p>Verification may include checks such as:
 * <ul>
 *     <li>Service and action consistency</li>
 *     <li>Party constraints</li>
 * </ul>
 */
public interface ConnectorBusinessMessageVerifier {
    /**
     * Verifies the given message against the rules of the given verification mode.
     *
     * <p>The method returns normally if the message complies. If it does not, the failure is
     * reported by an exception.
     *
     * @param message          the connector message to verify (must not be null)
     * @param verificationMode the processing mode verification mode that determines which rules are
     *                         applied (must not be null)
     */
    void verify(
        @NonNull ConnectorMessage message,
        @NonNull ProcessingModeVerificationMode verificationMode);
}
