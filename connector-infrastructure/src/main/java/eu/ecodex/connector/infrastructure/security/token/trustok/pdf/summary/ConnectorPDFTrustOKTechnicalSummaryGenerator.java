/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.trustok.pdf.summary;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import eu.ecodex.connector.infrastructure.security.exception.ConnectorTrustOKTokenException;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenAESType;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.ecodex.connector.infrastructure.security.model.token.signature.ConnectorTokenSignature;
import eu.ecodex.connector.infrastructure.security.token.trustok.pdf.PDFDocumentFonts;
import java.io.ByteArrayOutputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generates a technical PDF summary for connector token validation results.
 */
@Slf4j
@Component
public class ConnectorPDFTrustOKTechnicalSummaryGenerator
        extends ConnectorPDFTrustOKSummarySupport
        implements ConnectorPDFTrustOKSummaryGenerator {
    private static final String SUCCESS_MESSAGE = "Successful";
    private static final String FAILURE_MESSAGE = "Fail";

    private static String boolToResult(boolean value) {
        return value ? SUCCESS_MESSAGE : FAILURE_MESSAGE;
    }

    @Override
    public ByteArrayOutputStream generate(ConnectorToken token) {
        log.debug("Creating TrustOK PDF technical summary");

        var pdfFonts = PDFDocumentFonts.createFonts();
        var outputStream = new ByteArrayOutputStream();
        var writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_1_7);

        try (
                var writer = new PdfWriter(outputStream, writerProperties);
                var pdfDocument = new PdfDocument(writer);
                var document = new Document(pdfDocument)
        ) {
            document.setFontSize(10);
            document.setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, 1.0f));
            addPageHeader(document, token, pdfFonts);
            addBody(document, token, pdfFonts);
        } catch (Exception e) {
            log.error("Failed to generate TrustOK PDF technical summary");
            throw new ConnectorTrustOKTokenException(
                    "Failed to generate TrustOK PDF technical summary", e
            );
        }

        return outputStream;
    }

    private void addPageHeader(Document document, ConnectorToken token, PDFDocumentFonts pdfFonts) {
        document.add(generateHeader());
        document.add(addTechnicalSummaryTitle(pdfFonts));
        document.add(addGeneralInfo(token));
    }

    private void addBody(Document document, ConnectorToken token, PDFDocumentFonts pdfFonts) {
        var signatures = resolveSignatures(token);
        var advancedSystemType = token.getAdvancedElectronicSystem();

        if (advancedSystemType == ConnectorTokenAESType.AUTHENTICATION_BASED) {
            addAuthenticationBasedContent(document, token, signatures, pdfFonts);
        } else {
            addSignatureBasedContent(document, token, signatures, pdfFonts);
        }
    }

    private void addNewPage(Document document, ConnectorToken token, PDFDocumentFonts pdfFonts) {
        document.add(new AreaBreak());
        addPageHeader(document, token, pdfFonts);
    }

    private void addAuthenticationBasedContent(
            Document document,
            ConnectorToken token,
            List<ConnectorTokenSignature> signatures,
            PDFDocumentFonts pdfFonts) {
        boolean signaturesComplete = signatures != null && !signatures.isEmpty()
                                     && signatures.getFirst().getCertificateInformation() != null
                                     && signatures.getFirst().getSignatureInformation() != null
                                     && signatures.getFirst().getSigningTime() != null
                                     && signatures.getFirst().getTechnicalResult() != null;

        if (signaturesComplete) {
            for (var i = 0; i < signatures.size(); i++) {
                if (i > 0) {
                    addNewPage(document, token, pdfFonts);
                }
                var signature = signatures.get(i);
                document.add(addSignatureNumber(i + 1, signatures.size(), pdfFonts));
                document.add(addSignatureInfo(signature));
                document.add(addCertificateInfo(signature));
                document.add(addTechnicalInfo(signature).setMarginBottom(30));
                document.add(addValidationStamp(signature.getTechnicalResult().getTrustLevel()));
            }
        } else {
            if (signatures != null && !signatures.isEmpty()) {
                log.warn("Authentication-based signatures present but incomplete — "
                         + "falling back to token-level authentication info");
            }
            document.add(addAuthenticationInfo(token));
            document.add(addTechnicalInfo(token));
            document.add(addValidationStamp(token.getTechnicalValidationResultTrustLevel()));
        }
    }

    private void addSignatureBasedContent(
            Document document,
            ConnectorToken token,
            List<ConnectorTokenSignature> signatures,
            PDFDocumentFonts pdfFonts) {
        if (signatures != null && !signatures.isEmpty()) {
            for (var i = 0; i < signatures.size(); i++) {
                if (i > 0) {
                    addNewPage(document, token, pdfFonts);
                }
                var signature = signatures.get(i);
                document.add(addSignatureNumber(i + 1, signatures.size(), pdfFonts));
                document.add(addSignatureInfo(signature));
                document.add(addCertificateInfo(signature));

                // Backward compatibility: if single signature has no technical result,
                // fall back to token-level result
                boolean useFallback = signatures.size() == 1 && (
                        signature.getTechnicalResult() == null
                        || signature.getTechnicalResult().getTrustLevel() == null);

                if (useFallback) {
                    document.add(addTechnicalInfo(token));
                    document.add(addValidationStamp(
                            token.getTechnicalValidationResultTrustLevel()));
                } else {
                    document.add(addTechnicalInfo(signature));
                    document.add(addValidationStamp(
                            signature.getTechnicalResult().getTrustLevel()));
                }
            }
        } else {
            document.add(addTechnicalInfo(token));
            document.add(addValidationStamp(token.getTechnicalValidationResultTrustLevel()));
        }
    }

    /**
     * Null-safe extraction of the signature list from token.
     */
    private List<ConnectorTokenSignature> resolveSignatures(ConnectorToken token) {
        var validation = token.getValidation();

        if (validation == null) {
            return null;
        }

        var verification = validation.getVerificationData();

        if (verification == null) {
            return null;
        }

        return verification.getSignatureData();
    }

    private Table addGeneralInfo(ConnectorToken token) {
        var info = createTwoColumnsTable();
        info.addCell(leftCell("General Information"));

        var issuer = token.getIssuer();

        var country = issuer.getCountry();
        info.addCell(rightCell("Issuing Country"));
        info.addCell(rightCell(getTextOrNA(country)));

        info.addCell(rightCell("Advanced Electronic System"));
        info.addCell(rightCell(getTextOrNA(issuer.getAdvancedElectronicSystem().getText())));

        var documentInfo = String.format(
                "%s, %s",
                getTextOrNA(token.getDocument().getType()),
                getTextOrNA(token.getDocument().getFilename())
        );
        info.addCell(rightCell("Document Information"));
        info.addCell(rightCell(documentInfo));

        info.addCell(rightCell("Time of Issuance"));
        info.addCell(rightCell(formatDateOrNA(token.getValidationVerificationTime())));

        return info;
    }

    private Image addValidationStamp(ConnectorTokenTechnicalTrustLevel trustLevel) {
        var stamp = switch (trustLevel) {
            case ConnectorTokenTechnicalTrustLevel.SUCCESSFUL -> createSuccessStamp();
            case ConnectorTokenTechnicalTrustLevel.SUFFICIENT -> createSufficientStamp();
            default -> createFailureStamp();
        };

        return stamp.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    }

    private Paragraph addTechnicalSummaryTitle(PDFDocumentFonts pdfFonts) {
        return addTitle("Technical Assessment of the Validation Report", pdfFonts)
                .setMarginBottom(25);
    }

    private Paragraph addSignatureNumber(int counter, int size, PDFDocumentFonts pdfFonts) {
        return addParagraph(String.format("Signature %s of %s", counter, size))
                .setMarginBottom(20)
                .setFont(pdfFonts.bold())
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Table addSignatureInfo(ConnectorTokenSignature signature) {
        var info = createTwoColumnsTable();

        info.addCell(leftCell("Signature Information"));

        var signatureInformation = signature.getSignatureInformation();

        if (signatureInformation != null) {
            final var signingTime = formatDateOrNA(signature.getSigningTime());

            info.addCell(rightCell("Signing Time"));
            info.addCell(rightCell(signingTime));

            info.addCell(rightCell("Structure Verification"));
            info.addCell(rightCell(boolToResult(signatureInformation.isStructureValid())));

            info.addCell(rightCell("Signature Verification"));
            info.addCell(rightCell(boolToResult(signatureInformation.isSignatureValid())));

            info.addCell(rightCell("Signature Level"));
            info.addCell(rightCell(getTextOrNA(signatureInformation.getLevel())));
        } else {
            // Just in case. Should never happen as the token structure is invalid when the
            // signature attributes are missing.
            info.addCell(
                    centerCell("Signature Attributes missing. No signature information available!")
            );
        }

        return info;
    }

    private Table addAuthenticationInfo(ConnectorToken token) {
        var info = createTwoColumnsTable();

        info.addCell(leftCell("Authentication Information"));

        info.addCell(rightCell("Identity Provider"));
        info.addCell(rightCell(
                getTextOrNA(token.getValidationVerificationAuthenticationProvider())));

        info.addCell(rightCell("Username Synonym"));
        info.addCell(rightCell(
                getTextOrNA(token.getValidationVerificationAuthenticationUsername())));

        info.addCell(rightCell("Time of Authentication"));
        info.addCell(rightCell(
                formatDateOrNA(token.getValidationVerificationAuthenticationTime())));

        return info;
    }

    private Table addTechnicalInfo(ConnectorTokenSignature signature) {
        var result = signature.getTechnicalResult();
        if (result == null) {
            throw new IllegalStateException(
                    "getTechnicalResult() is null — caller must guard before calling this method"
            );
        }

        var info = createTwoColumnsTable();

        final var trustLevel = result.getTrustLevel();
        info.addCell(rightCell("Validation of the Document"));
        info.addCell(rightCell(getTextOrNA(trustLevel == null ? null : trustLevel.getText())));

        info.addCell(rightCell("Comment"));
        info.addCell(rightCell(
                getTextOrNA(result.getComment() == null ? "" : result.getComment())));

        return info;
    }

    private Table addTechnicalInfo(ConnectorToken token) {
        var info = createTwoColumnsTable();
        info.addCell(leftCell("Technical Result"));

        // get data from structure
        var trustLevel = token.getTechnicalValidationResultTrustLevel();

        info.addCell(rightCell("Validation of the Document"));
        info.addCell(rightCell(getTextOrNA(trustLevel == null ? null : trustLevel.getText())));

        var validation = token.getValidation();
        var verification = (validation != null) ? validation.getVerificationData() : null;
        var signatures = (verification != null) ? verification.getSignatureData() : null;

        if (signatures == null || signatures.isEmpty()) {
            var comment = (validation != null && validation.getTechnicalResult() != null)
                          ? validation.getTechnicalResult().getComment()
                          : null;

            info.addCell(rightCell("Comment"));
            info.addCell(rightCell(getTextOrNA(
                    comment == null ? "Unable to find signatures" : comment)));
        }

        return info;
    }

    private Table addCertificateInfo(ConnectorTokenSignature signature) {
        var info = createTwoColumnsTable();
        info.addCell(leftCell("Certificate information"));

        var certificateInfo = signature != null ? signature.getCertificateInformation() : null;

        if (certificateInfo == null) {
            info.addCell(centerCell("N/A"));

            return info;
        }

        var certificateOwner = getTextOrNA(certificateInfo.getSubject());
        var certificateIssuer = getTextOrNA(certificateInfo.getIssuer());

        info.addCell(rightCell("Signatory"));
        info.addCell(rightCell(certificateOwner));

        info.addCell(rightCell("Issuer"));
        info.addCell(rightCell(certificateIssuer));

        info.addCell(rightCell("Certificate Verification"));
        info.addCell(rightCell(boolToResult(certificateInfo.isValid())));

        info.addCell(rightCell("Validity At Signing Time"));
        info.addCell(rightCell(boolToResult(certificateInfo.isValidityAtSigningTime())));

        return info;
    }
}
