/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.token.trustok.pdf.summary;

import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorToken;
import java.io.ByteArrayOutputStream;

/**
 * An interface that defines the structure for creating a PDF summary from a given
 * {@link ConnectorToken}. Classes implementing this interface are responsible for generating a PDF
 * document that encapsulates relevant information extracted from the provided token.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Encapsulates summary information to represent different aspects of the token such as
 *     technical, legal, or signature details.
 * </ul>
 */
public interface ConnectorPDFTrustOKSummaryGenerator {
    ByteArrayOutputStream generate(ConnectorToken token);
}
