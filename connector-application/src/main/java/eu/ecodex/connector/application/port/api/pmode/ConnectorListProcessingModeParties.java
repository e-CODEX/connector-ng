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

import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingModeParty;
import jakarta.annotation.Nonnull;
import java.util.List;


/**
 * Use case for listing the parties configured for the processing modes of a business domain.
 */
public interface ConnectorListProcessingModeParties {
    /**
     * Retrieves the processing mode parties associated with the given business domain.
     *
     * @param businessDomainIdentifier identifier of the business domain whose processing mode
     *                                 parties are listed (must not be null)
     *
     * @return the list of {@link ConnectorProcessingModeParty} objects configured for the given
     *     business domain; an empty list if none are configured, never {@code null}
     */
    List<ConnectorProcessingModeParty> execute(@Nonnull String businessDomainIdentifier);
}
