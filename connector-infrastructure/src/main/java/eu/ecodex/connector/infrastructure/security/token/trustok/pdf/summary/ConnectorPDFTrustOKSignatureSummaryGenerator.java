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
import com.lowagie.text.DocumentException;
import eu.ecodex.connector.infrastructure.security.exception.ConnectorTrustOKTokenException;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.security.token.trustok.pdf.PDFDocumentFonts;
import eu.ecodex.connector.infrastructure.security.token.trustok.pdf.TitleStyle;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.simplereport.SimpleReport;
import java.io.ByteArrayOutputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Generates a PDF summary appendix for connector token signature validation.
 */
@Slf4j
@Component
public class ConnectorPDFTrustOKSignatureSummaryGenerator extends ConnectorPDFTrustOKSummarySupport
        implements ConnectorPDFTrustOKSummaryGenerator {
    @Override
    public ByteArrayOutputStream generate(ConnectorToken token) {
        final var validationToken = token.getValidation();
        final var report = validationToken.getOriginalValidationReport();
        var reports = report.getReports();
        var diagnosticData = reports.getDiagnosticData();
        var simpleReport = reports.getSimpleReport();

        log.debug("Creating TrustOK PDF appendix summary");

        var pdfFonts = PDFDocumentFonts.createFonts();
        var outputStream = new ByteArrayOutputStream();
        var writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_1_7);

        try (
                var writer = new PdfWriter(outputStream, writerProperties);
                var pdfDocument = new PdfDocument(writer);
                var document = new Document(pdfDocument)
        ) {
            document.setFontSize(10);

            document.add(addParagraph("Time information", TitleStyle.HEADER1, pdfFonts));
            document.add(addParagraph(
                    "Verification Time: " + formatDateOrNA(simpleReport.getValidationTime()))
            );

            var index = 1;

            for (var signatureId : simpleReport.getSignatureIdList()) {
                writeSignatureInformation(
                        document,
                        diagnosticData,
                        simpleReport,
                        signatureId,
                        index++,
                        pdfFonts
                );
            }
        } catch (Exception e) {
            log.error("Failed to generate TrustOK PDF appendix summary", e);
            throw new ConnectorTrustOKTokenException(
                    "Failed to generate TrustOK PDF appendix summary", e
            );
        }

        return outputStream;
    }

    private void writeSignatureInformation(
            Document document,
            DiagnosticData diagnosticData,
            SimpleReport simpleReport,
            final String signatureId,
            int index,
            PDFDocumentFonts pdfFonts) {
        document.add(addParagraph("Signature information " + index, TitleStyle.HEADER1, pdfFonts));
        document.add(addParagraph(
                "Signature verification",
                simpleReport.isValid(signatureId), TitleStyle.DEFAULT, pdfFonts
        ));
        document.add(addParagraph("Signature algorithm: "
                                  + diagnosticData.getSignatureEncryptionAlgorithm(signatureId)));
        document.add(addParagraph(
                "Certificate Path Revocation Analysis", TitleStyle.HEADER2, pdfFonts));

        var certificateChain = diagnosticData.getSignatureCertificateChain(signatureId);

        var chainValid = !certificateChain.isEmpty()
                         && certificateChain
                                 .stream()
                                 .allMatch(id -> diagnosticData.getCertificateRevocationStatus(id)
                                                               .isGood()
                                 );

        document.add(addParagraph("Summary", chainValid, null, pdfFonts));
        document.add(addParagraph("Certificate Verification", TitleStyle.HEADER3, pdfFonts));

        if (certificateChain.isEmpty()) {
            document.add(addParagraph("No Certificate Verification is available!"));
        } else {
            certificateChain.forEach(
                    id -> writeCertificateVerification(document, id, diagnosticData, pdfFonts));
        }

        var signingCertificateId = diagnosticData.getSigningCertificateId(signatureId);

        document.add(addParagraph("Signature Level Analysis", TitleStyle.HEADER2, pdfFonts));
        var signatureFormat = diagnosticData.getSignatureFormat(signatureId);
        if (signatureFormat == null) {
            document.add(addParagraph("No Signature Level Analysis is available."));
        } else {
            document.add(addParagraph("Signature format: " + signatureFormat));
            writeLevelBES(
                    document,
                    signatureId,
                    signingCertificateId,
                    diagnosticData,
                    certificateChain,
                    pdfFonts
            );
            writeLevelEPES(document, signatureId, diagnosticData, pdfFonts);

            writeSignatureLevel(
                    document, "T", signatureId, diagnosticData,
                    diagnosticData.isThereTLevel(signatureId),
                    diagnosticData.isTLevelTechnicallyValid(signatureId),
                    pdfFonts
            );
            writeSignatureLevel(
                    document, "X", signatureId, diagnosticData,
                    diagnosticData.isThereXLevel(signatureId),
                    diagnosticData.isXLevelTechnicallyValid(signatureId),
                    pdfFonts
            );
            writeSignatureLevel(
                    document, "A", signatureId, diagnosticData,
                    diagnosticData.isThereALevel(signatureId),
                    diagnosticData.isALevelTechnicallyValid(signatureId),
                    pdfFonts
            );
        }

        document.add(addParagraph("Final Conclusion", TitleStyle.HEADER2, pdfFonts));
        document.add(addParagraph("The signature is: "
                                  + simpleReport.getSignatureQualification(signatureId).name()));
    }

    private void writeCertificateVerification(
            Document document,
            String certificateId,
            DiagnosticData diagnosticData,
            PDFDocumentFonts pdfFonts) throws DocumentException {
        document.add(addParagraph(
                diagnosticData.getCertificateDN(certificateId), TitleStyle.HEADER5, pdfFonts));
        document.add(addParagraph(
                "Issuer name: " + diagnosticData.getCertificateIssuerDN(certificateId)));
        document.add(addParagraph(
                "Serial Number: " + diagnosticData.getCertificateSerialNumber(certificateId)));
        document.add(addParagraph(
                "Validity at validation time: " + diagnosticData.isValidCertificate(certificateId))
        );
        document.add(addParagraph(
                "Certificate Revocation status: " + diagnosticData.getCertificateRevocationStatus(
                        certificateId))
        );
    }

    private void writeLevelBES(
            final Document document,
            final String signatureId,
            String signingCertificateId,
            final DiagnosticData diagnosticData,
            List<String> certificateChain,
            PDFDocumentFonts pdfFonts) throws DocumentException {
        if (!diagnosticData.isSigningCertificateIdentified(signatureId)) {
            log.debug(
                    "Signing certificate not identified for [{}] — skipping BES level output",
                    signatureId
            );
            document.add(addParagraph("Signature Level BES", false, TitleStyle.HEADER3, pdfFonts));
            return;
        }

        var signatureDate = diagnosticData.getSignatureDate(signatureId);
        document.add(addParagraph(
                "Signature Level BES", signatureDate != null, TitleStyle.HEADER3, pdfFonts));
        document.add(addParagraph("Signing certificate: "
                                  + diagnosticData.getCertificateIssuerDN(signingCertificateId)));
        document.add(addParagraph("Signing time: " + formatDateOrNA(signatureDate)));
        document.add(addParagraph("Certificates", TitleStyle.HEADER4, pdfFonts));
        document.add(addParagraph(
                "Number of certificates in the chain: " + certificateChain.size()));
    }

    private void writeLevelEPES(
            final Document document,
            String signatureId,
            final DiagnosticData diagnosticData,
            PDFDocumentFonts pdfFonts) throws DocumentException {
        var policyId = diagnosticData.getPolicyId(signatureId);
        var hasPolicy = StringUtils.hasText(policyId);
        document.add(addParagraph("Signature Level EPES", hasPolicy, TitleStyle.HEADER3, pdfFonts));
        if (hasPolicy) {
            document.add(addParagraph("Signature policy: " + policyId));
        }
    }

    /**
     * Shared implementation for T, X, and A-levels which follow an identical structure.
     */
    private void writeSignatureLevel(
            Document document,
            String levelName,
            String signatureId,
            DiagnosticData diagnosticData,
            boolean present,
            boolean technicallyValid,
            PDFDocumentFonts pdfFonts) {
        document.add(addParagraph(
                "Signature Level " + levelName,
                present && technicallyValid,
                TitleStyle.HEADER3,
                pdfFonts
        ));

        if (!present) {
            return;
        }

        var timestampIds = diagnosticData.getTimestampIdList(signatureId);
        document.add(addParagraph("Number of timestamps found: " + timestampIds.size()));
        timestampIds.forEach(tsId -> {
            document.add(addParagraph("Timestamp id: " + tsId, TitleStyle.HEADER5, pdfFonts));
            document.add(addParagraph(
                    "Timestamp type: " + diagnosticData.getTimestampType(tsId)));
        });
    }
}
