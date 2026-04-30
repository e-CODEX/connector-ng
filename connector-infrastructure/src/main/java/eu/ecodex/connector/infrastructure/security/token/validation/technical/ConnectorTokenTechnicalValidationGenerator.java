/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.validation.technical;

import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenValidation;
import eu.europa.esig.dss.model.DSSDocument;

/**
 * Generates a technical validation result for an embedded or a detached digital signature.
 *
 * <p>This component is responsible for producing a {@link ConnectorTokenValidation}
 * based on:
 * <ul>
 *     <li>The original business document</li>
 *     <li>The detached signature associated with that document</li>
 * </ul>
 *
 * <p>Implementations typically perform cryptographic and structural validation
 * of the signature using DSS libraries, including checks such as:
 * <ul>
 *     <li>Signature integrity</li>
 *     <li>Certificate validity</li>
 *     <li>Trust chain validation</li>
 * </ul>
 *
 * <p>This interface is intended as a low-level technical validation step and
 * does not necessarily include business-level validation rules.
 */
public interface ConnectorTokenTechnicalValidationGenerator {
    /**
     * Generates a technical validation result for a given document and detached signature.
     *
     * @param businessDocument  the original signed business document
     * @param detachedSignature the detached signature to validate against the document
     *
     * @return a {@link ConnectorTokenValidation} describing validation results
     * @throws Exception if validation fails due to parsing, cryptographic, or DSS errors
     */
    ConnectorTokenValidation generate(DSSDocument businessDocument, DSSDocument detachedSignature)
            throws Exception;

    boolean supportsAuthenticationBased();
}
