/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.model.container;

import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorToken;
import eu.europa.esig.dss.model.DSSDocument;
import lombok.Builder;

/**
 * Immutable container aggregating all artefacts related to a Connector exchange.
 *
 * @param businessContent the original business payload
 * @param token           the connector token containing validation and metadata
 * @param tokenXML        XML representation of the trust OK token
 * @param tokenPDF        PDF representation of the trust OK token (e.g. human-readable report)
 * @param asicDocument    final ASiC container including signed artefacts
 */
@Builder
public record ConnectorContainer(
    ConnectorContainerBusinessContent businessContent,
    ConnectorToken token,
    DSSDocument tokenXML,
    DSSDocument tokenPDF,
    DSSDocument asicDocument
) {
}
