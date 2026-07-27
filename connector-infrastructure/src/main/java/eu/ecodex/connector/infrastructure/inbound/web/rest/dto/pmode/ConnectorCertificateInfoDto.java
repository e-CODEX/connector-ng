/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode;

import java.time.Instant;

/**
 * Represents a Data Transfer Object (DTO) for storing information related to a connector's
 * certificate. This class encapsulates details about a certificate, such as its alias, type,
 * subject, issuer, validity period, and algorithm details.
 *
 * @param alias              The alias associated with the certificate in the keystore or
 *                           truststore.
 * @param entryType          The type of the certificate entry (e.g., trusted, private key).
 * @param subject            The subject distinguished name (DN) of the certificate holder.
 * @param issuer             The issuer distinguished name (DN) of the certificate authority.
 * @param serialNumber       The serial number of the certificate as assigned by the issuer.
 * @param notBefore          The timestamp indicating the start of the certificate's validity
 *                           period.
 * @param notAfter           The timestamp indicating the expiry of the certificate's validity
 *                           period.
 * @param signatureAlgorithm The algorithm used to sign the certificate.
 * @param keyAlgorithm       The algorithm used for the public key contained in the certificate.
 * @param validity           A textual representation of the certificate's validity period.
 */
public record ConnectorCertificateInfoDto(
    String alias,
    String entryType,
    String subject,
    String issuer,
    String serialNumber,
    Instant notBefore,
    Instant notAfter,
    String signatureAlgorithm,
    String keyAlgorithm,
    String validity
) {
}
