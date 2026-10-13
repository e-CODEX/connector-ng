/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.businessdomain;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import jakarta.annotation.Nonnull;


/**
 * Use case for verifying a business domain.
 */
public interface ConnectorBusinessDomainVerifier {
    /**
     * Verifies the business domain identified by the given identifier.
     *
     * <p>The method returns normally if the verification succeeds. If it fails, the failure is
     * reported by an exception.
     *
     * @param identifier identifier of the business domain to verify (must not be null)
     */
    void execute(@Nonnull ConnectorBusinessDomainIdentifier identifier);
}
