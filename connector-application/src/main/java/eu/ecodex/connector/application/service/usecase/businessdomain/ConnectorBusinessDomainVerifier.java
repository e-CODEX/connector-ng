/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.businessdomain;

import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotEnabledException;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import jakarta.annotation.Nonnull;

/**
 * Service interface for checking whether a business domain is active and can accept messages.
 */
public interface ConnectorBusinessDomainVerifier {
    /**
     * Asserts that the business domain identified by {@code identifier} exists and is enabled.
     *
     * @param identifier the business domain identifier to check; must not be null
     * @throws ConnectorBusinessDomainNotFoundException   if no business domain matches the
     *                                                    identifier
     * @throws ConnectorBusinessDomainNotEnabledException if the business domain is disabled
     */
    void execute(@Nonnull ConnectorBusinessDomainIdentifier identifier);
}
