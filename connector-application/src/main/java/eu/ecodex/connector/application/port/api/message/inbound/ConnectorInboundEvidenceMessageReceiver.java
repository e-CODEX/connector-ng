/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.inbound;

import jakarta.annotation.Nonnull;

/**
 * Defines the contract for processing inbound evidence messages within the connector system.
 * Implementations of this interface handle the execution of logic specific to evidence messages,
 * which are typically used for providing proof or validation in a business context.
 */
public interface ConnectorInboundEvidenceMessageReceiver {
    /**
     * Executes the processing of an inbound evidence message command.
     *
     * @param command The command encapsulating the inbound evidence message details.
     */
    void execute(@Nonnull ConnectorInboundEvidenceMessageCommand command);
}
