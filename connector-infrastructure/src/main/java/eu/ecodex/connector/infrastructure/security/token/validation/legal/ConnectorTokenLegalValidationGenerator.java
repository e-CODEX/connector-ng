/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.validation.legal;

import eu.ecodex.connector.infrastructure.security.exception.ConnectorTokenException;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenAESType;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenLegalTrustLevel;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenLegalValidationResult;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.ecodex.connector.infrastructure.security.model.token.signature.ConnectorTokenSignature;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generates a legal validation report for a connector token.
 */
@Slf4j
@Component
public class ConnectorTokenLegalValidationGenerator {
    /**
     * Creates a {@link ConnectorTokenLegalValidationResult} based on the provided
     * {@link ConnectorToken}. This method validates the technical and legal aspects of the token,
     * using either authentication-based or signature-based resolution strategies.
     *
     * @param token the connector token to be validated. Must include a validation object and a
     *              valid technical validation result. If any of these are missing, an exception is
     *              thrown.
     *
     * @return the legal validation result of the specified token, including calculated trust level
     *         and any corresponding disclaimers.
     * @throws ConnectorTokenException if either the validation object or the technical
     *                                     validation result is missing from the token.
     */
    public ConnectorTokenLegalValidationResult create(final ConnectorToken token) {
        var validation = token.getValidation();

        if (validation == null) {
            log.warn("No validation object found in token");
            throw new ConnectorTokenException("no validation object found");
        }

        var technicalValidationResult = validation.getTechnicalResult();

        if (technicalValidationResult == null) {
            log.warn("No technical validation result found in token");
            throw new ConnectorTokenException("no technical validation result found");
        }

        final var legalValidationResult = new ConnectorTokenLegalValidationResult();
        final var technicalTrustLevel = technicalValidationResult.getTrustLevel();
        final var verificationData = validation.getVerificationData();
        final var signatures = verificationData != null
                               ? verificationData.getSignatureData()
                               : null;
        final var aes = token.getAdvancedElectronicSystem();

        if (aes == ConnectorTokenAESType.AUTHENTICATION_BASED) {
            resolveAuthenticationBased(
                    token,
                    signatures,
                    technicalTrustLevel,
                    legalValidationResult
            );
        } else {
            resolveSignatureBased(signatures, technicalTrustLevel, legalValidationResult);
        }

        log.info(
                "Technical trust-level is {}} -> legal trust-level {}",
                technicalTrustLevel, legalValidationResult.getTrustLevel()
        );

        return legalValidationResult;
    }

    private void resolveAuthenticationBased(
            ConnectorToken token,
            List<ConnectorTokenSignature> signatures,
            ConnectorTokenTechnicalTrustLevel technicalTrustLevel,
            ConnectorTokenLegalValidationResult legalValidationResult) {
        boolean technicallySuccessful =
                ConnectorTokenTechnicalTrustLevel.SUCCESSFUL.equals(technicalTrustLevel);

        boolean singleValidAuthCert =
                signatures != null
                && signatures.size() == 1
                && signatures.getFirst().getAuthenticationCertificate() != null
                && signatures.getFirst().getAuthenticationCertificate()
                             .isValidationSuccessful();

        if (singleValidAuthCert && technicallySuccessful) {
            setResult(
                    legalValidationResult,
                    ConnectorTokenLegalTrustLevel.SUCCESSFUL,
                    Disclaimers.AUTH_APPROVED
            );
        } else if (signatures != null && signatures.size() > 1) {
            setResult(
                    legalValidationResult,
                    ConnectorTokenLegalTrustLevel.NOT_SUCCESSFUL,
                    Disclaimers.AUTH_TOO_MANY_SIGNATURES
            );
        } else if (!technicallySuccessful) {
            setResult(
                    legalValidationResult,
                    ConnectorTokenLegalTrustLevel.NOT_SUCCESSFUL,
                    Disclaimers.NOT_APPROVED
            );
        } else if ((signatures == null || signatures.isEmpty())
                   && token.getValidationVerificationAuthenticationData() != null
                   && technicallySuccessful) {
            setResult(
                    legalValidationResult,
                    ConnectorTokenLegalTrustLevel.SUCCESSFUL,
                    Disclaimers.APPROVED
            );
        } else {
            // signatures null/empty, no auth data, or unexpected combination
            log.warn(
                    "Authentication-based token fell through all branches — "
                    + "signatures: {}, authData: {}, trustLevel: {}",
                    signatures == null ? "null" : signatures.size(),
                    token.getValidationVerificationAuthenticationData(),
                    technicalTrustLevel
            );
            setResult(
                    legalValidationResult,
                    ConnectorTokenLegalTrustLevel.NOT_SUCCESSFUL,
                    Disclaimers.AUTH_PROVIDER_NOT_VERIFIED
            );
        }
    }

    private void resolveSignatureBased(
            List<ConnectorTokenSignature> signatures,
            ConnectorTokenTechnicalTrustLevel technicalTrustLevel,
            ConnectorTokenLegalValidationResult legalValidationResult) {
        var validCount = 0;
        var invalidCount = 0;

        if (signatures != null) {
            for (var signature : signatures) {
                var technicalResult = signature.getTechnicalResult();

                if (technicalResult == null) {
                    log.warn("Signature has no technical result — excluded from validity count");
                    continue;
                }

                if (technicalResult.getTrustLevel()
                    == ConnectorTokenTechnicalTrustLevel.SUCCESSFUL) {
                    validCount++;
                } else {
                    invalidCount++;
                }
            }
        }

        if (invalidCount == 0 && validCount == 0) {
            // No signatures with technical results — fall back to token-level trust
            if (technicalTrustLevel == ConnectorTokenTechnicalTrustLevel.SUCCESSFUL) {
                setResult(
                        legalValidationResult,
                        ConnectorTokenLegalTrustLevel.SUCCESSFUL,
                        Disclaimers.APPROVED
                );
            } else {
                setResult(
                        legalValidationResult,
                        ConnectorTokenLegalTrustLevel.NOT_SUCCESSFUL,
                        Disclaimers.NOT_APPROVED
                );
            }
        } else if (invalidCount > 0 && validCount > 0) {
            setResult(
                    legalValidationResult,
                    ConnectorTokenLegalTrustLevel.NOT_SUCCESSFUL,
                    Disclaimers.MIXED_SIGNATURES
            );
        } else if (validCount > 0) {
            setResult(
                    legalValidationResult,
                    ConnectorTokenLegalTrustLevel.SUCCESSFUL,
                    Disclaimers.APPROVED
            );
        } else {
            setResult(
                    legalValidationResult,
                    ConnectorTokenLegalTrustLevel.NOT_SUCCESSFUL,
                    Disclaimers.NOT_APPROVED
            );
        }
    }

    private void setResult(
            ConnectorTokenLegalValidationResult result,
            ConnectorTokenLegalTrustLevel level,
            String disclaimer) {
        result.setTrustLevel(level);
        result.setDisclaimer(disclaimer);
    }

    /**
     * Legal disclaimer strings.
     */
    private static final class Disclaimers {
        static final String APPROVED =
                "e-CODEX approves the validity of the document. It is attested that it fulfils the "
                + "requirements to be legally valid in the sending country.";
        static final String AUTH_APPROVED =
                "e-CODEX approves the validity of the document. The signature on the document "
                + "has been created by a valid authentication service provider. It is "
                + "attested that the document fulfils the requirements to be legally "
                + "valid in the sending country.";
        static final String NOT_APPROVED =
                "e-CODEX does not approve the validity of the document.";
        static final String AUTH_TOO_MANY_SIGNATURES =
                "e-CODEX does not approve the validity of the document. There are too many "
                + "signatures of authentication service providers present.";
        static final String AUTH_PROVIDER_NOT_VERIFIED =
                "e-CODEX does not approve the validity of the document. The signatory of the "
                + "document could not be verified as being a valid authentication service "
                + "provider!";
        static final String MIXED_SIGNATURES =
                "e-CODEX cannot approve the validity of the document as there are valid "
                + "and invalid signatures present on the document. Please check the "
                + "technical validation report!";

        private Disclaimers() {
        }
    }
}
