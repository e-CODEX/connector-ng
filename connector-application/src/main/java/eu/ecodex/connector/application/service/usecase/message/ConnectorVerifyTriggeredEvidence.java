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
 * Defines a contract for a verification process triggered by evidence messages.
 *
 * <p>
 * This interface represents a component responsible for performing verification operations related
 * to evidence messages in the context of connector functionality. Implementers of this interface
 * are expected to provide the concrete logic for the `verify` method to handle validation and
 * business rule verification processes.
 */
public interface ConnectorVerifyTriggeredEvidence {
    /**
     * Performs verification based on the provided trigger message.
     *
     * <p>
     * This method validates and processes the given trigger message to ensure compliance with
     * predefined rules and criteria.
     *
     * @param triggerMessage the message triggering the verification process, containing the
     *                       necessary data for validation.
     */
    void verify(@Nonnull ConnectorMessage triggerMessage);
}
