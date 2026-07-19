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

import eu.ecodex.connector.application.exception.ConnectorMessagePartyException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Verifier responsible for validating the parties involved in a {@link ConnectorMessage}.
 *
 * <p>This component ensures that the sender, receiver, and any other
 * relevant parties contained in the message meet the expected requirements (e.g. presence, format,
 * authorization, or consistency).
 *
 * <p>If the verification fails, an exception should be thrown to indicate
 * that the message cannot proceed further in the processing pipeline.</p>
 */
public interface ConnectorMessagePartiesVerifier {
    /**
     * Verifies that the parties contained in the provided {@link ConnectorMessage} are valid,
     * according to the implemented validation rules.
     *
     * @param message the connector message whose parties must be verified
     *
     * @throws ConnectorMessagePartyException if the parties are missing, invalid, or violate
     *                                        verification rules
     */
    void verify(@Nonnull ConnectorMessage message);
}
