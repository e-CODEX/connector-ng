/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.model.token;

import eu.ecodex.connector.infrastructure.security.model.token.signature.ConnectorTokenTechnicalValidationResult;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Connector Token aggregating document, issuer, and validation information.
 */
@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConnectorTokenType", propOrder = {"issuer", "document", "validation"})
public class ConnectorToken implements Serializable {
    @XmlElement(name = "Issuer", required = true)
    private ConnectorTokenIssuer issuer;
    @XmlElement(name = "Document", required = true)
    private ConnectorTokenDocument document;
    @XmlElement(name = "Validation", required = true)
    private ConnectorTokenValidation validation;

    /**
     * Returns the advanced electronic system (AES) associated with the issuer.
     *
     * @return AES type, or {@code null} if issuer is not defined
     */
    public ConnectorTokenAESType getAdvancedElectronicSystem() {
        return (issuer == null) ? null : issuer.getAdvancedElectronicSystem();
    }

    /**
     * Returns the technical validation result.
     *
     * @return technical validation result, or {@code null} if unavailable
     */
    public ConnectorTokenTechnicalValidationResult getTechnicalValidationResult() {
        return (validation == null) ? null : validation.getTechnicalResult();
    }

    /**
     * Returns authentication data used during validation verification.
     *
     * @return authentication data, or {@code null} if unavailable
     */
    public ConnectorTokenAuthenticationData getValidationVerificationAuthenticationData() {
        final var verificationData = getValidationVerificationData();
        return (verificationData == null) ? null : verificationData.getAuthenticationData();
    }

    /**
     * Returns verification data associated with validation.
     *
     * @return verification data, or {@code null} if unavailable
     */
    public ConnectorTokenVerificationData getValidationVerificationData() {
        return (validation == null) ? null : validation.getVerificationData();
    }

    /**
     * Returns the original validation report container.
     *
     * @return original validation report, or {@code null} if unavailable
     */
    public ConnectorTokenOriginalValidationReportContainer getValidationOriginalReport() {
        return (validation == null) ? null : validation.getOriginalValidationReport();
    }

    /**
     * Returns the time at which validation verification was performed.
     *
     * @return verification time, or {@code null} if unavailable
     */
    public XMLGregorianCalendar getValidationVerificationTime() {
        return (validation == null) ? null : validation.getVerificationTime();
    }

    /**
     * Returns the legal validation result.
     *
     * @return legal validation result, or {@code null} if unavailable
     */
    public ConnectorTokenLegalValidationResult getLegalValidationResult() {
        return (validation == null) ? null : validation.getLegalResult();
    }

    /**
     * Returns the legal trust level.
     *
     * <p>If no legal validation result is available, defaults to
     * {@link ConnectorTokenLegalTrustLevel#NOT_SUCCESSFUL}.
     *
     * @return legal trust level (never {@code null})
     */
    public ConnectorTokenLegalTrustLevel getLegalValidationResultTrustLevel() {
        var legalValidationResult = getLegalValidationResult();
        return legalValidationResult == null
               ? ConnectorTokenLegalTrustLevel.NOT_SUCCESSFUL
               : legalValidationResult.getTrustLevel();
    }

    /**
     * Returns the disclaimer associated with the legal validation result.
     *
     * @return disclaimer text, or {@code null} if unavailable
     */
    public String getLegalValidationResultDisclaimer() {
        final var legalValidationResult = getLegalValidationResult();
        return (legalValidationResult == null) ? null : legalValidationResult.getDisclaimer();
    }

    /**
     * Returns the identity provider used during authentication.
     *
     * @return identity provider name, or {@code null} if unavailable
     */
    public String getValidationVerificationAuthenticationProvider() {
        final var authenticationData = getValidationVerificationAuthenticationData();
        return (authenticationData == null) ? null : authenticationData.getIdentityProvider();
    }

    /**
     * Returns the username (or synonym) used during authentication.
     *
     * @return username, or {@code null} if unavailable
     */
    public String getValidationVerificationAuthenticationUsername() {
        final var authenticationData = getValidationVerificationAuthenticationData();
        return (authenticationData == null) ? null : authenticationData.getUsernameSynonym();
    }

    /**
     * Returns the time of authentication used during validation verification.
     *
     * @return authentication time, or {@code null} if unavailable
     */
    public XMLGregorianCalendar getValidationVerificationAuthenticationTime() {
        final var authenticationData = getValidationVerificationAuthenticationData();
        return (authenticationData == null) ? null : authenticationData.getTimeOfAuthentication();
    }

    /**
     * Returns the technical trust level.
     *
     * <p>If no technical validation result is available, defaults to
     * {@link ConnectorTokenTechnicalTrustLevel#FAIL}.
     *
     * @return technical trust level (never {@code null})
     */
    public ConnectorTokenTechnicalTrustLevel getTechnicalValidationResultTrustLevel() {
        final var validationResult = getTechnicalValidationResult();
        return (validationResult == null)
               ? ConnectorTokenTechnicalTrustLevel.FAIL
               : validationResult.getTrustLevel();
    }
}
