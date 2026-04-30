/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.util;

import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.signature.AdvancedSignature;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import javax.security.auth.x500.X500Principal;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.util.StringUtils;

/**
 * Utility class providing helper methods for DSS-based technical validation.
 */
public class TechnicalValidationUtil {
    private static final DatatypeFactory DATATYPE_FACTORY;
    private static final String QUAL_IS_ADES = "QUAL_IS_ADES";
    private static final String QUAL_IS_ADES_IND = "QUAL_IS_ADES_IND";
    private static final String STATUS_OK = "OK";

    static {
        try {
            DATATYPE_FACTORY = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private TechnicalValidationUtil() {
    }

    /**
     * Extracts the signing time of a signature as an {@link XMLGregorianCalendar}.
     *
     * @param signature the DSS signature
     *
     * @return signing time, or {@code null} if unavailable
     */
    public static XMLGregorianCalendar getSigningTime(AdvancedSignature signature) {
        if (signature == null) {
            return null;
        }

        return toXMLGregorianCalendar(signature.getSigningTime());
    }

    private static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        if (date == null) {
            return null;
        }

        var calendar = new GregorianCalendar();
        calendar.setTime(date);

        return DATATYPE_FACTORY.newXMLGregorianCalendar(calendar);
    }

    /**
     * Retrieves a {@link CertificateToken} from a signature by its DSS identifier.
     *
     * @param signature     the signature containing certificates
     * @param certificateId DSS certificate identifier
     *
     * @return matching certificate token, or {@code null} if not found
     */
    public static CertificateToken getCertificateToken(
            AdvancedSignature signature,
            String certificateId) {
        if (signature == null) {
            return null;
        }

        return signature.getCertificates().stream()
                        .filter(c -> c.getDSSId().asXmlId().equals(certificateId))
                        .findFirst()
                        .orElse(null);
    }

    /**
     * Extracts the {@link X509Certificate} from a {@link CertificateToken}.
     *
     * @param certificateToken the certificate token
     *
     * @return underlying certificate, or {@code null} if token is null
     */
    public static X509Certificate getCertificate(final CertificateToken certificateToken) {
        return certificateToken == null ? null : certificateToken.getCertificate();
    }

    /**
     * Finds a {@link CertificateWrapper} by issuer name.
     *
     * @param usedCertificates list of certificates from diagnostic data
     * @param issuerName       issuer distinguished name
     *
     * @return matching wrapper, or {@code null} if not found
     */
    public static CertificateWrapper getCertificateWrapper(
            List<CertificateWrapper> usedCertificates, String issuerName) {
        if (usedCertificates == null || !StringUtils.hasText(issuerName)) {
            return null;
        }

        for (CertificateWrapper certificateWrapper : usedCertificates) {
            certificateWrapper.getCertificateDN().equals(issuerName);
            return certificateWrapper;
        }

        return null;
    }

    /**
     * Retrieves the issuer certificate token of the given certificate token. This method resolves
     * to the same certificate token if the given certificate is self-signed, otherwise it fetches
     * the token of its issuer.
     *
     * @param certificateToken the certificate token for which the issuer certificate token is to be
     *                         retrieved; may be {@code null}.
     *
     * @return the issuer certificate token, or {@code null} if the input is {@code null}.
     */
    public static CertificateToken getIssuerCertificateToken(CertificateToken certificateToken) {
        if (certificateToken == null) {
            return null;
        }
        if (certificateToken.isSelfSigned()) {
            return certificateToken;
        }

        var certificate = getIssuerCertificate(certificateToken);

        if (certificate == null) {
            return null;
        }

        return new CertificateToken(certificate);
    }

    /**
     * Retrieves the issuer's X509 certificate for a given certificate token.
     *
     * @param certificateToken the certificate token whose issuer certificate is to be retrieved;
     *                         may be {@code null}.
     *
     * @return the X509 certificate of the issuer, or {@code null} if the issuer certificate token
     *         is not found or the input is {@code null}.
     */
    public static X509Certificate getIssuerCertificate(CertificateToken certificateToken) {
        var issuerCertificateToken = getIssuerCertificateToken(certificateToken);

        if (issuerCertificateToken == null) {
            return null;
        }

        return issuerCertificateToken.getCertificate();
    }

    /**
     * Retrieves the issuer name of the given X509 certificate in RFC1779 format.
     *
     * @param certificate the X509 certificate from which the issuer name is to be extracted; may be
     *                    {@code null}.
     *
     * @return the issuer name in RFC1779 format, or {@code null} if the input certificate is
     *         {@code null}.
     */
    public static String getCertificateIssuerName(X509Certificate certificate) {
        return certificate == null ? null
                                   : certificate.getIssuerX500Principal()
                                                .getName(X500Principal.RFC1779);
    }

    /**
     * Retrieves the subject name of the given X509 certificate in RFC1779 format.
     *
     * @param certificate the X509 certificate from which the subject name is to be extracted; may
     *                    be {@code null}.
     *
     * @return the subject name in RFC1779 format, or {@code null} if the input certificate is
     *         {@code null}.
     */
    public static String getCertificateSubjectName(X509Certificate certificate) {
        return certificate == null ? null
                                   : certificate.getSubjectX500Principal()
                                                .getName(X500Principal.RFC1779);
    }

    /**
     * Retrieves the signature qualification (e.g. QES, AdES).
     *
     * @param simpleReport DSS simple report
     * @param signatureId  signature identifier
     *
     * @return signature qualification, or {@link SignatureQualification#NA} if unavailable
     */
    public static String getSignatureFormatLevelAsString(
            SimpleReport simpleReport,
            String signatureId) {
        return getSignatureFormatLevel(simpleReport, signatureId)
                .map(SignatureLevel::toString)
                .orElse(null);
    }

    /**
     * Retrieves the signature format level of a specified signature from a given SimpleReport.
     *
     * @param simpleReport the DSS simple report containing signature details; can be {@code null}.
     * @param signatureId  the identifier of the signature whose format level is to be retrieved.
     *
     * @return an {@code Optional} containing the signature format level if available, otherwise an
     *         empty {@code Optional}.
     */
    public static Optional<SignatureLevel> getSignatureFormatLevel(
            SimpleReport simpleReport,
            String signatureId) {
        if (simpleReport == null) {
            return Optional.empty();
        }

        var format = simpleReport.getSignatureFormat(signatureId);

        return Optional.ofNullable(format);
    }

    /**
     * Retrieves the signature qualification conclusion for the specified signature from a given
     * SimpleReport. If the SimpleReport is null, the method returns
     * {@link SignatureQualification#NA}.
     *
     * @param simpleReport the DSS simple report containing signature details; may be {@code null}.
     * @param signatureId  the identifier of the signature whose qualification is to be retrieved.
     *
     * @return the signature qualification, or {@link SignatureQualification#NA} if the SimpleReport
     *         is {@code null}.
     */
    public static SignatureQualification getSignatureConclusion(
            SimpleReport simpleReport,
            String signatureId) {
        if (simpleReport == null) {
            return SignatureQualification.NA;
        }

        return simpleReport.getSignatureQualification(signatureId);
    }

    /**
     * Checks whether the signature computation is valid.
     *
     * @param simpleReport DSS simple report
     * @param signatureId  signature identifier
     *
     * @return {@code true} if signature is mathematically valid
     */
    public static boolean checkSignatureCorrectness(SimpleReport simpleReport, String signatureId) {
        return simpleReport != null && simpleReport.isValid(signatureId);
    }

    /**
     * Checks the revocation status of a given certificate and determines the technical trust
     * level.
     *
     * @param certificateToken the certificate token to be verified; must not be null.
     * @param revoked          a boolean indicating whether the certificate has been revoked.
     *
     * @return {@code ConnectorTokenTechnicalTrustLevel.FAIL} if the certificate is null or revoked;
     *         {@code ConnectorTokenTechnicalTrustLevel.SUCCESSFUL} if the certificate is not
     *         revoked.
     */
    public static ConnectorTokenTechnicalTrustLevel checkCertificateRevocation(
            CertificateToken certificateToken, boolean revoked) {
        if (certificateToken == null) {
            return ConnectorTokenTechnicalTrustLevel.FAIL;
        }

        return revoked
               ? ConnectorTokenTechnicalTrustLevel.FAIL
               : ConnectorTokenTechnicalTrustLevel.SUCCESSFUL;
    }

    /**
     * Checks the revocation status of a digital certificate and determines the appropriate trust
     * level based on the certificate's attributes.
     *
     * @param certificateToken the digital certificate token to be checked for revocation; must not
     *                         be null.
     *
     * @return a {@code ConnectorTokenTechnicalTrustLevel} indicating the trust level:
     *         <ul>
     *             <li>{@code FAIL} if the provided certificate token is null.
     *             <li>{@code SUCCESSFUL} if the certificate is self-signed.
     *             <li>{@code SUFFICIENT} if the certificate is not self-signed.
     *         </ul>
     */
    public static ConnectorTokenTechnicalTrustLevel checkCertificateRevocation(
            CertificateToken certificateToken) {
        if (certificateToken == null) {
            return ConnectorTokenTechnicalTrustLevel.FAIL;
        }

        return certificateToken.isSelfSigned()
               ? ConnectorTokenTechnicalTrustLevel.SUCCESSFUL
               : ConnectorTokenTechnicalTrustLevel.SUFFICIENT;
    }

    /**
     * Checks the validity of a certificate token at a given signing time and determines the
     * corresponding technical trust level.
     *
     * @param certificateToken the certificate token to validate; must not be null. If null, the
     *                         method will return {@code ConnectorTokenTechnicalTrustLevel.FAIL}.
     * @param signingTime      the signing time against which the validity of the certificate token
     *                         will be checked.
     *
     * @return {@code ConnectorTokenTechnicalTrustLevel.SUCCESSFUL} if the certificate token is
     *         valid at the given signing time, otherwise
     *         {@code ConnectorTokenTechnicalTrustLevel.FAIL}.
     */
    public static ConnectorTokenTechnicalTrustLevel checkCertificateValidity(
            CertificateToken certificateToken, XMLGregorianCalendar signingTime) {
        if (certificateToken == null) {
            return ConnectorTokenTechnicalTrustLevel.FAIL;
        }

        return certificateToken.isValidOn(signingTime.toGregorianCalendar().getTime())
               ? ConnectorTokenTechnicalTrustLevel.SUCCESSFUL
               : ConnectorTokenTechnicalTrustLevel.FAIL;
    }

    /**
     * Checks whether a certificate is marked as trusted in the diagnostic data.
     *
     * @param diagnosticData the DSS diagnostic data
     * @param certificateId  certificate identifier
     *
     * @return {@code true} if certificate is trusted, otherwise {@code false}
     */
    public static boolean checkTrustAnchor(DiagnosticData diagnosticData, String certificateId) {
        if (diagnosticData == null) {
            return false;
        }

        var certificates = diagnosticData.getJaxbModel().getUsedCertificates();

        if (certificates == null) {
            return false;
        }

        return certificates.stream()
                           .anyMatch(xmlCert -> certificateId.equals(xmlCert.getId())
                                                && xmlCert.isTrusted());
    }

    /**
     * Evaluates whether a signature qualification is acceptable.
     *
     * <p>Rules:
     * <ul>
     *   <li>Explicit AdES/QES levels → valid</li>
     *   <li>Non-AdES levels → invalid</li>
     *   <li>{@code NA} → fallback to detailed report analysis</li>
     * </ul>
     *
     * <p>For {@code NA}, the method inspects the {@link DetailedReport} for constraints
     * such as {@code QUAL_IS_ADES} or {@code QUAL_IS_ADES_IND} with status {@code OK}.
     *
     * @param simpleReport   DSS simple report
     * @param detailedReport DSS detailed report
     * @param signatureId    signature identifier
     *
     * @return {@code true} if the signature is considered acceptable
     */
    public static boolean checkSignatureConclusion(
            SimpleReport simpleReport, DetailedReport detailedReport, String signatureId) {
        var conclusion = getSignatureConclusion(simpleReport, signatureId);
        return switch (conclusion) {
            case NA -> checkNAConclusion(detailedReport, signatureId);
            case ADESEAL, ADESEAL_QC, ADESIG, ADESIG_QC,
                 INDETERMINATE_ADESEAL, INDETERMINATE_ADESEAL_QC,
                 INDETERMINATE_ADESIG, INDETERMINATE_ADESIG_QC,
                 INDETERMINATE_QESEAL, INDETERMINATE_QESIG,
                 QESIG, QESEAL -> true;
            default -> false;
        };
    }

    /**
     * For NA qualification, inspects the detailed report for QUAL_IS_ADES or QUAL_IS_ADES_IND
     * constraints with OK status, which indicate the signature is still considered AdES-compliant.
     */
    private static boolean checkNAConclusion(
            DetailedReport detailedReport, String signatureId) {
        if (detailedReport == null) {
            return false;
        }

        var sig = detailedReport.getXmlSignatureById(signatureId);
        if (sig == null) {
            return false;
        }

        var qualification = sig.getValidationSignatureQualification();
        if (qualification == null) {
            return false;
        }

        var constraints = qualification.getConstraint();
        if (constraints == null || constraints.isEmpty()) {
            return false;
        }

        return constraints.stream().anyMatch(c -> {
            var nameValue = c.getName() != null ? c.getName().getValue() : null;
            var statusValue = c.getStatus() != null ? c.getStatus().value() : null;
            return STATUS_OK.equals(statusValue)
                   && (QUAL_IS_ADES.equals(nameValue) || QUAL_IS_ADES_IND.equals(nameValue));
        });
    }
}
