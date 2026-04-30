/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.trustok;

import eu.ecodex.connector.infrastructure.security.model.token.ConnectorToken;
import eu.europa.esig.dss.model.DSSDocument;
import jakarta.annotation.Nonnull;

/**
 * Represents an interface for creating a Trust-OK token in the context of the Connector
 * infrastructure. Implementations of this interface are responsible for generating a digitally
 * signed Trust-OK token document based on the provided {@link ConnectorToken}.
 *
 * <p>A Trust-OK token is a structured, signed document that validates certain trust-related
 * aspects (e.g. legal or technical) within the Connector framework.
 */
public interface ConnectorTrustOKTokenGenerator {
    DSSDocument generate(@Nonnull ConnectorToken token);
}
