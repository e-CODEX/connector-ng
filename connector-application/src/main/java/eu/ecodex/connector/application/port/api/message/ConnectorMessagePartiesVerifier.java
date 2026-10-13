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

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Verifier responsible for validating the parties involved in a {@link ConnectorMessage}.
 *
 * <p>This component ensures that the sender, receiver, and any other relevant parties contained
 * in the message meet the expected requirements.
 *
 * <p>If the verification fails, an exception is thrown to indicate that the message cannot
 * proceed further in the processing pipeline.
 */
public interface ConnectorMessagePartiesVerifier {
    /**
     * Verifies the parties of the given message.
     *
     * <p>The method returns normally if the parties meet the requirements. If they do not, the
     * failure is reported by an exception.
     *
     * @param message the connector message whose parties are verified (must not be null)
     */
    void verify(@Nonnull ConnectorMessage message);
}
