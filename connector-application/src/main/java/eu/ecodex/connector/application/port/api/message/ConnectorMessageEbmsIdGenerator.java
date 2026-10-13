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

/**
 * Generates unique ebMS message identifiers for connector messages.
 */
public interface ConnectorMessageEbmsIdGenerator {
    /**
     * Generates a new, unique ebMS message identifier.
     *
     * <p>The identifier has the format {@code <uuid>@<suffix>}, where {@code <uuid>} is a randomly
     * generated UUID and {@code <suffix>} is a configured domain-like suffix.
     *
     * @return a new unique ebMS message identifier; never {@code null}
     */
    String execute();
}
