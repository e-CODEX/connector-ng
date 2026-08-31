/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.container.checks;

import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainer;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainerBusinessContent;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenAESType;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenAuthenticationData;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenDocument;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenLegalValidationResult;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenValidation;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenVerificationData;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.signature.ConnectorTokenTechnicalValidationResult;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Checker for the {@link ConnectorContainer}.
 */
@Slf4j
@Order(1)
@Component("ConnectorContainerChecker")
public class ConnectorContainerChecker implements ConnectorMessageContainerChecker {
    @Override
    public void check(@NonNull ConnectorContainer object) {
        checkDocumentPresent(object.asicDocument(), "ASiC-S container");
        checkBusinessContent(object.businessContent());
        checkDocumentPresent(object.tokenPDF(), "PDF token");
        checkDocumentPresent(object.tokenXML(), "XML token");
        checkToken(object.token());
    }

    /**
     * A DSS document that must be present and carry content.
     */
    private void checkDocumentPresent(DSSDocument document, String label) {
        if (isMissing(document)) {
            throw new IllegalStateException(label + " must not be null");
        }
        if (DSSUtils.isEmpty(document)) {
            throw new IllegalStateException(label + " must not be empty");
        }
    }

    private void checkBusinessContent(ConnectorContainerBusinessContent businessContent) {
        checkDocumentPresent(businessContent.getDocument(), "Business document");

        var detachedSignature = businessContent.getDetachedSignature();
        if (!isMissing(detachedSignature) && DSSUtils.isEmpty(detachedSignature)) {
            throw new IllegalStateException(
                "Business document detached signature must not be empty");
        }
    }

    private void checkToken(ConnectorToken token) {
        if (isMissing(token)) {
            throw new IllegalStateException("Token must not be null");
        }
        checkTokenDocument(token.getDocument());

        if (isMissing(token.getIssuer())) {
            throw new IllegalStateException("Token issuer must not be null");
        }
        checkTokenValidity(token.getValidation());
        checkTokenAdvancedSystemType(token);
    }

    private void checkTokenDocument(ConnectorTokenDocument tokenDocument) {
        if (isMissing(tokenDocument)) {
            throw new IllegalStateException("Token document must not be null");
        }
        if (isMissing(tokenDocument.getFilename())) {
            throw new IllegalStateException("Token document filename must not be null");
        }
        if (isMissing(tokenDocument.getType())) {
            throw new IllegalStateException("Token document type must not be null");
        }
        if (isMissing(tokenDocument.getDigestMethod())) {
            throw new IllegalStateException("Token document digest method must not be null");
        }
        if (isMissing(tokenDocument.getDigestValue())) {
            throw new IllegalStateException("Token document digest value must not be null");
        }
    }

    private void checkTokenValidity(ConnectorTokenValidation validation) {
        if (isMissing(validation)) {
            throw new IllegalStateException("Token validation must not be null");
        }
        if (isMissing(validation.getVerificationTime())) {
            throw new IllegalStateException("Token validation verification time must not be null");
        }
        if (isMissing(validation.getVerificationData())) {
            throw new IllegalStateException("Token validation verification data must not be null");
        }
        checkTokenTechnicalValidity(validation.getTechnicalResult());
        checkTokenLegalValidity(validation.getLegalResult());
    }

    private void checkTokenTechnicalValidity(ConnectorTokenTechnicalValidationResult result) {
        if (isMissing(result)) {
            throw new IllegalStateException("Token technical validation must not be null");
        }
        var trustLevel = result.getTrustLevel();
        if (isMissing(trustLevel)) {
            throw new IllegalStateException(
                "Token technical validation trust level must not be null");
        }
        if (trustLevel != ConnectorTokenTechnicalTrustLevel.SUCCESSFUL
            && isMissing(result.getComment())) {
            log.warn(
                "Token technical validation comment is missing for non-successful trust level"
            );
        }
    }

    private void checkTokenLegalValidity(ConnectorTokenLegalValidationResult validationResult) {
        if (isMissing(validationResult)) {
            throw new IllegalStateException("Token legal validation must not be null");
        }
        if (isMissing(validationResult.getTrustLevel())) {
            throw new IllegalStateException("Token legal validation trust level must not be null");
        }
        if (isMissing(validationResult.getDisclaimer())) {
            log.info("Token legal validation disclaimer is missing");
        }
    }

    private void checkTokenAdvancedSystemType(ConnectorToken token) {
        var aesType = token.getAdvancedElectronicSystem();
        if (isMissing(aesType)) {
            throw new IllegalStateException("Token advanced system type must not be null");
        }

        var verificationData = token.getValidationVerificationData();
        if (isMissing(verificationData)) {
            // AES-type checks are skipped when verification data is absent. Confirm this is
            // intended — if getValidationVerificationData() is the same object already asserted
            // non-null in checkTokenValidity, this branch is unreachable and can go.
            return;
        }
        if (aesType == ConnectorTokenAESType.AUTHENTICATION_BASED) {
            checkTokenAuthBasedAESType(verificationData);
        } else if (aesType == ConnectorTokenAESType.SIGNATURE_BASED) {
            checkTokenSignatureBasedAESType(verificationData);
        }
    }

    private void checkTokenAuthBasedAESType(ConnectorTokenVerificationData verificationData) {
        validateAuthenticationData(verificationData.getAuthenticationData());

        // An authentication-based token may additionally carry signatures; validate them if present
        var signatureData = verificationData.getSignatureData();
        if (!isMissing(signatureData) && !signatureData.isEmpty()) {
            checkTokenSignatureBasedAESType(verificationData);
        }
    }

    private void validateAuthenticationData(ConnectorTokenAuthenticationData authenticationData) {
        if (isMissing(authenticationData)) {
            throw new IllegalStateException(
                "Token authentication data must not be null when the system is AUTHENTICATION_BASED"
            );
        }
        if (isMissing(authenticationData.getUsernameSynonym())) {
            throw new IllegalStateException(
                "Token authentication username synonym must not be null when the system is "
                    + "AUTHENTICATION_BASED"
            );
        }
        if (isMissing(authenticationData.getIdentityProvider())) {
            throw new IllegalStateException(
                "Token authentication identity provider must not be null when the system is "
                    + "AUTHENTICATION_BASED"
            );
        }
        if (isMissing(authenticationData.getTimeOfAuthentication())) {
            throw new IllegalStateException(
                "Token authentication time of authentication must not be null when the system is "
                    + "AUTHENTICATION_BASED"
            );
        }
    }

    private void checkTokenSignatureBasedAESType(ConnectorTokenVerificationData verificationData) {
        var signatureData = verificationData.getSignatureData();
        if (isMissing(signatureData) || signatureData.isEmpty()) {
            throw new IllegalStateException(
                "Token signature data must not be null when the system is SIGNATURE_BASED");
        }

        for (var signature : signatureData) {
            var cert = signature.getCertificateInformation();
            var attributes = signature.getSignatureInformation();
            var time = signature.getSigningTime();

            // Legacy escape hatch: a single, entirely empty entry means the document was
            // unsigned (behavior as of version 1.08.4).
            boolean legacyUnsigned = isMissing(cert) && isMissing(attributes)
                && isMissing(time) && signatureData.size() == 1;
            if (legacyUnsigned) {
                log.warn("No token signature data validation needed. Document is unsigned "
                             + "in the way it was up to version 1.08.4");
                continue;
            }

            if (isMissing(cert)) {
                throw new IllegalStateException(
                    "Token signature certificate information must not be null when the system is "
                        + "SIGNATURE_BASED"
                );
            }
            if (isMissing(cert.getIssuer())) {
                throw new IllegalStateException(
                    "Token signature certificate issuer must not be null when the system is "
                        + "SIGNATURE_BASED"
                );
            }
            if (isMissing(attributes)) {
                throw new IllegalStateException(
                    "Token signature attributes must not be null when the system is SIGNATURE_BASED"
                );
            }
            if (isMissing(attributes.getFormat())) {
                throw new IllegalStateException(
                    "Token signature format must not be null when the system is SIGNATURE_BASED");
            }
            if (isMissing(attributes.getLevel())) {
                throw new IllegalStateException(
                    "Token signature level must not be null when the system is SIGNATURE_BASED");
            }
            if (isMissing(time)) {
                throw new IllegalStateException(
                    "Token signature signing time must not be null when the system is "
                        + "SIGNATURE_BASED"
                );
            }
        }
    }

    private boolean isMissing(Object object) {
        return object == null;
    }

    private boolean isMissing(byte[] object) {
        return object == null || object.length == 0;
    }
}
