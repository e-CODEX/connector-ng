/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.pmode;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import lombok.NonNull;

/**
 * Use case for verifying the processing modes of a business domain.
 */
public interface ConnectorProcessingModeVerifier {
    /**
     * Verifies the processing modes configured for the given business domain.
     *
     * <p>The method returns normally if the verification succeeds. If it fails, the failure is
     * reported by an exception.
     *
     * @param identifier identifier of the business domain whose processing modes are verified
     *                   (must not be null)
     */
    void execute(@NonNull ConnectorBusinessDomainIdentifier identifier);
}
