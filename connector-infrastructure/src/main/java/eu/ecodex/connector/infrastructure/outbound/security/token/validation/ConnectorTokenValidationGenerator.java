/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.token.validation;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.infrastructure.dss.ConnectorDssCertificateVerifier;
import eu.ecodex.connector.infrastructure.dss.ConnectorDssTrustedListLoader;
import eu.ecodex.connector.infrastructure.outbound.security.exception.ConnectorTokenException;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenAESType;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenAuthenticationData;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenDocument;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenIssuer;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenLegalValidationResult;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenValidation;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenVerificationData;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.signature.ConnectorTokenAuthenticationCertificate;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.signature.ConnectorTokenSignature;
import eu.ecodex.connector.infrastructure.outbound.security.token.validation.legal.ConnectorTokenLegalValidationGenerator;
import eu.ecodex.connector.infrastructure.outbound.security.token.validation.technical.ConnectorTokenValidationFactory;
import eu.ecodex.connector.infrastructure.outbound.security.util.DSSDocumentUtil;
import eu.ecodex.connector.infrastructure.outbound.security.util.DigestUtil;
import eu.ecodex.connector.infrastructure.property.businessdocument.ConnectorBusinessDocumentProperties;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateValidity;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.xmldsig.jaxb.DigestMethodType;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generates a fully populated {@link ConnectorToken} by orchestrating technical and legal
 * validation processes.
 *
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Executing technical validation of the business document</li>
 *     <li>Enriching validation data (e.g. authentication-based handling)</li>
 *     <li>Constructing the token document metadata (digest, filenames, etc.)</li>
 *     <li>Executing legal validation</li>
 *     <li>Ensuring consistency and completeness of the resulting token</li>
 * </ul>
 * </p>
 *
 * <p>
 * It also integrates Trusted List (TSL) validation for authentication-based
 * certificates when applicable.
 * </p>
 */
@Slf4j
@Component
public class ConnectorTokenValidationGenerator {
    private final ConnectorTokenValidationFactory tokenValidationFactory;
    private final ConnectorTokenLegalValidationGenerator legalValidationGenerator;
    private final ConnectorDssCertificateVerifier certificateVerifier;
    private final TrustedListsCertificateSource trustedListsCertificateSource;

    /**
     * Constructs a new validation token generator.
     *
     * @param tokenValidationFactory     factory for technical validation generators
     * @param trustedListLoader          loader for Trusted List (TSL) certificate sources
     * @param legalValidationGenerator   generator for legal validation results
     * @param certificateVerifier        DSS certificate verifier
     * @param businessDocumentProperties configuration used to resolve TSL sources
     */
    public ConnectorTokenValidationGenerator(
        ConnectorTokenValidationFactory tokenValidationFactory,
        ConnectorDssTrustedListLoader trustedListLoader,
        ConnectorTokenLegalValidationGenerator legalValidationGenerator,
        ConnectorDssCertificateVerifier certificateVerifier,
        ConnectorBusinessDocumentProperties businessDocumentProperties) {
        this.tokenValidationFactory = tokenValidationFactory;
        this.legalValidationGenerator = legalValidationGenerator;
        this.certificateVerifier = certificateVerifier;

        var sourceName = businessDocumentProperties.getSignature()
                                                   .getValidation().getTrustedListSource();
        this.trustedListsCertificateSource = trustedListLoader.getCertificateSource(sourceName);
    }

    /**
     * Creates a {@link ConnectorToken} from the given inputs.
     *
     * <p>The process includes:
     * <ol>
     *     <li>Running technical validation
     *     <li>Validating the technical result structure
     *     <li>Handling authentication-based validation (if applicable)
     *     <li>Building token document metadata (digest, filenames)
     *     <li>Running legal validation
     *     <li>Validating the legal result structure
     * </ol>
     *
     * @param message           the connector message driving validation
     * @param businessDocument  the primary document
     * @param detachedSignature optional detached signature
     * @param issuer            token issuer metadata
     *
     * @return a fully populated {@link ConnectorToken}
     *
     * @throws ConnectorTokenException if validation or token construction fails
     */
    public ConnectorToken createToken(
        ConnectorMessage message,
        DSSDocument businessDocument,
        DSSDocument detachedSignature,
        ConnectorTokenIssuer issuer) {
        try {
            var technicalValidation = this.tokenValidationFactory.createTechnicalValidation(
                message);
            var token = new ConnectorToken();
            token.setIssuer(issuer);

            var tokenValidation = technicalValidation.generate(
                businessDocument, detachedSignature
            );
            checkTokenValidation(tokenValidation);
            token.setValidation(tokenValidation);

            var verificationData = tokenValidation.getVerificationData();
            var signatures = verificationData != null
                ? verificationData.getSignatureData()
                : null;

            if (issuer.getAdvancedElectronicSystem() == ConnectorTokenAESType.AUTHENTICATION_BASED
                && !technicalValidation.supportsAuthenticationBased()) {
                resolveAuthenticationBasedValidation(
                    tokenValidation,
                    verificationData,
                    signatures,
                    businessDocument,
                    detachedSignature
                );
            }

            // create the token document
            var tokenDocument = createTokenDocument(businessDocument, detachedSignature);
            token.setDocument(tokenDocument);

            var legalValidationResult = this.legalValidationGenerator.create(token);
            checkLegalValidation(legalValidationResult);
            tokenValidation.setLegalResult(legalValidationResult);

            return token;
        } catch (Exception e) {
            log.error("Failed to create token", e);
            throw new ConnectorTokenException("Failed to create token", e);
        }
    }

    private void resolveAuthenticationBasedValidation(
        ConnectorTokenValidation tokenValidation,
        ConnectorTokenVerificationData verificationData,
        List<ConnectorTokenSignature> signatures,
        DSSDocument businessDocument,
        DSSDocument detachedSignature) {
        var technicalResult = tokenValidation.getTechnicalResult();

        if (signatures != null && !signatures.isEmpty()) {
            if (signatures.size() > 1) {
                log.warn(
                    "Invalid number of authentication signatures: {} (only 1 allowed)",
                    signatures.size()
                );
                technicalResult.setTrustLevel(ConnectorTokenTechnicalTrustLevel.FAIL);
                technicalResult.setComment(
                    "Invalid count of signatures on the authentication data."
                );
                // tokenValidation.setTechnicalResult(technicalResult);
            } else {
                var authCertValidation = verifyAuthenticationCertificate(
                    businessDocument, detachedSignature
                );
                signatures.getFirst().setAuthenticationCertificate(authCertValidation);
            }
        } else {
            if (verificationData.getAuthenticationData() == null) {
                var authenticationData = new ConnectorTokenAuthenticationData();
                authenticationData.setIdentityProvider("Identity Provider missing!");
                authenticationData.setUsernameSynonym("Username Synonym missing!");

                try {
                    authenticationData.setTimeOfAuthentication(
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(
                            new GregorianCalendar()
                        )
                    );
                } catch (DatatypeConfigurationException e) {
                    throw new ConnectorTokenException(
                        "Failed to create authentication timestamp", e
                    );
                }

                verificationData.setAuthenticationData(authenticationData);
            }

            technicalResult.setTrustLevel(ConnectorTokenTechnicalTrustLevel.FAIL);
            technicalResult.setComment(
                "Neither authentication nor signature data present."
            );
            // tokenValidation.setTechnicalResult(technicalResult);
        }
    }

    /**
     * Builds the {@link ConnectorTokenDocument} metadata.
     *
     * <p>Includes filename, MIME type, optional signature filename,
     * and an SHA-256 digest of the document.
     *
     * @param businessDocument  the primary document
     * @param detachedSignature optional detached signature
     *
     * @return the populated token document descriptor
     */
    private ConnectorTokenDocument createTokenDocument(
        DSSDocument businessDocument, DSSDocument detachedSignature) throws IOException {
        var tokenDocument = new ConnectorTokenDocument();
        tokenDocument.setFilename(businessDocument.getName());

        if (businessDocument.getMimeType() != null) {
            tokenDocument.setType(businessDocument.getMimeType().getMimeTypeString());
        }

        if (detachedSignature != null) {
            tokenDocument.setSignatureFilename(detachedSignature.getName());
        }

        // note that this could be a configuration
        final var digestAlgorithm = DigestAlgorithm.SHA256;
        // SHA-256 used for document digest; hyphen form required for downstream compatibility
        var digestAlgorithmName = digestAlgorithm.getName().replace("SHA", "SHA-");

        var digestMethod = new DigestMethodType();
        digestMethod.setAlgorithm(digestAlgorithmName);
        tokenDocument.setDigestMethod(digestMethod);

        var businessDocumentData = DSSDocumentUtil.getDocumentData(businessDocument);
        tokenDocument.setDigestValue(DigestUtil.digest(businessDocumentData, digestAlgorithmName));

        return tokenDocument;
    }

    /**
     * Validates the presence and completeness of technical validation results.
     */
    private void checkTokenValidation(ConnectorTokenValidation validation) {
        if (validation == null) {
            throw new ConnectorTokenException(
                "Technical validation service did not produce a token validation object"
            );
        }

        var technicalResult = validation.getTechnicalResult();

        if (technicalResult == null) {
            throw new ConnectorTokenException(
                "Technical validation service did not produce a technical result");
        }

        if (technicalResult.getTrustLevel() == null) {
            throw new ConnectorTokenException(
                "Technical validation service did not set a trust level");
        }
    }

    /**
     * Validates the presence and completeness of legal validation results.
     */
    private void checkLegalValidation(ConnectorTokenLegalValidationResult result) {
        if (result == null) {
            throw new ConnectorTokenException(
                "Legal validation service did not produce a result");
        }

        if (result.getTrustLevel() == null) {
            throw new ConnectorTokenException(
                "Legal validation service did not set a trust level");
        }
    }

    /**
     * Verifies whether the authentication certificate is trusted based on TSL.
     *
     * <p>The certificate extracted from the document is compared against
     * certificates from the configured Trusted List source.
     */
    private ConnectorTokenAuthenticationCertificate verifyAuthenticationCertificate(
        DSSDocument businessDocument, DSSDocument detachedSignature) {
        var authCert = new ConnectorTokenAuthenticationCertificate();

        var signatureCertificate = getCertificate(businessDocument, detachedSignature);

        if (signatureCertificate == null) {
            log.warn(
                "Attribute authenticationCertificateTSL has not been set - Validation of "
                    + "signature certificate against TSL of authentication service providers will"
                    + " always fail!"
            );
            authCert.setValidationSuccessful(false);

            return authCert;
        }

        var tlsCertificates = trustedListsCertificateSource.getCertificates();

        if (tlsCertificates == null || tlsCertificates.isEmpty()) {
            log.warn(
                "TSL of authentication certificates was null or empty -"
                    + " No certificate will be deemed to be valid"
            );
            authCert.setValidationSuccessful(false);

            return authCert;
        }

        var subjectName = signatureCertificate.getSubjectX500Principal();
        // Match by certificate equality; subject name check is redundant if certs are equal
        boolean matched = tlsCertificates
            .stream()
            .map(CertificateToken::getCertificate)
            .anyMatch(cert ->
                          cert.equals(signatureCertificate)
                              && cert.getSubjectX500Principal().equals(subjectName));

        authCert.setValidationSuccessful(matched);

        return authCert;
    }

    /**
     * Extracts the signing certificate from the document.
     *
     * <p>Only embedded signatures are supported. Detached signatures are ignored. If multiple
     * signatures exist, the earliest signing certificate is selected.
     */
    private X509Certificate getCertificate(
        DSSDocument businessDocument, DSSDocument detachedSignature) {
        if (detachedSignature != null) {
            return null; // certificate extraction only applies to embedded signatures
        }

        try (var inputStream = businessDocument.openStream()) {
            var document = new InMemoryDocument(inputStream.readAllBytes());
            SignedDocumentValidator validator;

            try {
                validator = SignedDocumentValidator.fromDocument(document);
            } catch (DSSException e) {
                // Document type isn't recognized, too short, or unsigned — no certificate presents
                return null;
            }

            validator.setCertificateVerifier(certificateVerifier.getCommonCertificateVerifier());

            var report = validator.validateDocument();
            var certificateId = report.getDiagnosticData().getFirstSignatureId();
            // This could become troublesome with timezones
            // TODO: verify timezone handling is correct for cross-border scenarios

            Date earliestSignatureTime = null;

            X509Certificate signatureCertificate = null;

            for (var signature : validator.getSignatures()) {
                var signingTime = signature.getSigningTime();

                // Keep the earliest signature (note: timezone handling may need review)
                if (earliestSignatureTime == null || earliestSignatureTime.after(signingTime)) {
                    earliestSignatureTime = signingTime;

                    signatureCertificate = signature.getCandidatesForSigningCertificate()
                                                    .getCertificateValidityList()
                                                    .stream()
                                                    .map(CertificateValidity::getCertificateToken)
                                                    .filter(token -> token.getDSSIdAsString()
                                                                          .equals(certificateId))
                                                    .map(CertificateToken::getCertificate)
                                                    .findFirst()
                                                    .orElse(null);
                }
            }

            return signatureCertificate;
        } catch (Exception e) {
            log.error("Failed to extract certificate from business document", e);
            throw new ConnectorTokenException(
                "Failed to extract certificate from business document", e);
        }
    }
}
