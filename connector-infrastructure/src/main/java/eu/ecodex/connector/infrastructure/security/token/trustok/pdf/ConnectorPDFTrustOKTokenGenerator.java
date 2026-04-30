/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.trustok.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import eu.ecodex.connector.infrastructure.dss.ConnectorDssDocumentSigner;
import eu.ecodex.connector.infrastructure.dss.ConnectorDssSigningTokenProvider;
import eu.ecodex.connector.infrastructure.property.container.ConnectorContainerProperties;
import eu.ecodex.connector.infrastructure.security.exception.ConnectorTokenException;
import eu.ecodex.connector.infrastructure.security.exception.ConnectorTrustOKTokenException;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.security.token.trustok.ConnectorTrustOKTokenGenerator;
import eu.ecodex.connector.infrastructure.security.token.trustok.pdf.summary.ConnectorPDFTrustOKLegalSummaryGenerator;
import eu.ecodex.connector.infrastructure.security.token.trustok.pdf.summary.ConnectorPDFTrustOKSignatureSummaryGenerator;
import eu.ecodex.connector.infrastructure.security.token.trustok.pdf.summary.ConnectorPDFTrustOKTechnicalSummaryGenerator;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generates a signed "TrustOK" PDF token by assembling and merging multiple validation summaries.
 *
 * <p>This class orchestrates the creation of a final PDF document composed of:
 * <ul>
 *     <li>A legal summary</li>
 *     <li>A technical summary</li>
 *     <li>An optional signature appendix</li>
 * </ul>
 * </p>
 *
 * <p>The generated PDF is then digitally signed using a PAdES signature.
 *
 * <p>The order of the merged documents is strictly defined as:
 * <ol>
 *     <li>Legal summary
 *     <li>Technical summary
 *     <li>Signature appendix (if available)
 * </ol>
 */
@Slf4j
@Component
public class ConnectorPDFTrustOKTokenGenerator implements ConnectorTrustOKTokenGenerator {
    private final ConnectorPDFTrustOKLegalSummaryGenerator legalSummaryGenerator;
    private final ConnectorPDFTrustOKTechnicalSummaryGenerator technicalSummaryGenerator;
    private final ConnectorPDFTrustOKSignatureSummaryGenerator signatureSummaryGenerator;
    private final ConnectorDssDocumentSigner connectorDssDocumentSigner;
    private final ConnectorContainerProperties containerProperties;
    private final ConnectorDssSigningTokenProvider signingTokenProvider;

    /**
     * Constructs a new generator with all required dependencies.
     *
     * @param legalSummaryGenerator      generator for the legal summary PDF
     * @param technicalSummaryGenerator  generator for the technical summary PDF
     * @param signatureSummaryGenerator  generator for the signature appendix PDF
     * @param connectorDssDocumentSigner service used to apply the digital signature
     * @param containerProperties        configuration properties (including signing settings)
     */
    public ConnectorPDFTrustOKTokenGenerator(
            ConnectorPDFTrustOKLegalSummaryGenerator legalSummaryGenerator,
            ConnectorPDFTrustOKTechnicalSummaryGenerator technicalSummaryGenerator,
            ConnectorPDFTrustOKSignatureSummaryGenerator signatureSummaryGenerator,
            ConnectorDssDocumentSigner connectorDssDocumentSigner,
            ConnectorContainerProperties containerProperties) {
        this.legalSummaryGenerator = legalSummaryGenerator;
        this.technicalSummaryGenerator = technicalSummaryGenerator;
        this.signatureSummaryGenerator = signatureSummaryGenerator;
        this.connectorDssDocumentSigner = connectorDssDocumentSigner;
        this.containerProperties = containerProperties;

        var signature = containerProperties.getSignature();
        this.signingTokenProvider = new ConnectorDssSigningTokenProvider(
                signature.getKeystore(),
                signature.getPrivateKey()
        );
    }

    @Override
    public DSSDocument generate(@NonNull ConnectorToken token) {
        var legalSummaryPdf = this.legalSummaryGenerator.generate(token);
        var technicalSummaryPdf = this.technicalSummaryGenerator.generate(token);
        var appendixPdf = createAppendixPdf(token);

        try {
            // ! It is important to respect the order of the parameters.
            var mergedPdfs = mergeSummariesPdfs(legalSummaryPdf, technicalSummaryPdf, appendixPdf);
            var trustOkToken = new InMemoryDocument(mergedPdfs, "TrustOKToken", MimeTypeEnum.PDF);

            return sign(trustOkToken);
        } catch (ConnectorTrustOKTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new ConnectorTrustOKTokenException("Failed to generate TrustOK token PDF", e);
        }
    }

    private DSSDocument sign(DSSDocument pdfToken) {
        var signature = containerProperties.getSignature();
        return this.connectorDssDocumentSigner.signWithPadES(
                pdfToken,
                signature.getEncryptionAlgorithm(),
                signature.getDigestAlgorithm(),
                signingTokenProvider
        );
    }

    /**
     * Generates the signature appendix PDF.
     *
     * <p>Returns an empty stream if the token has no validation report or report data, which is a
     * valid case (the appendix is omitted from the merged + output).
     */
    private ByteArrayOutputStream createAppendixPdf(ConnectorToken token) {
        var validation = token.getValidation();

        if (validation == null) {
            log.warn("Token has no validation object — appendix will be omitted");
            throw new ConnectorTokenException(("Token does not contain a validation object"));
        }

        if (validation.getVerificationData() == null) {
            throw new ConnectorTokenException(
                    "Token validation must contain verification data"
            );
        }

        var report = validation.getOriginalValidationReport();

        if (report == null) {
            log.warn("Token has no validation report — appendix will be omitted");
            return new ByteArrayOutputStream(0);
        }

        var reportData = report.getAny();

        if (reportData == null || reportData.isEmpty()) {
            log.warn("Validation report contains no data — appendix will be omitted");
            return new ByteArrayOutputStream(0);
        }

        if (report.getReports() == null) {
            log.warn("Validation report has no report data — appendix will be omitted");
            return new ByteArrayOutputStream(0);
        }

        try {
            return signatureSummaryGenerator.generate(token);
        } catch (Exception e) {
            log.error("Failed to generate PDF appendix", e);
            throw new ConnectorTrustOKTokenException("Failed to generate PDF appendix", e);
        }
    }

    /**
     * Merges PDFs in the provided order. Empty or null streams are skipped.
     *
     * <p>Order is significant: legal summary → technical summary → signature appendix.
     */
    private byte[] mergeSummariesPdfs(ByteArrayOutputStream... pdfStreams) throws IOException {
        var mergedPdfStream = new ByteArrayOutputStream();

        try (var mergedPdf = new PdfDocument(new PdfWriter(mergedPdfStream))) {
            for (var stream : pdfStreams) {
                if (stream == null || stream.size() == 0) {
                    log.debug("Skipping empty PDF part during merge");
                    continue;
                }

                try (var src = new PdfDocument(
                        new PdfReader(new ByteArrayInputStream(stream.toByteArray())))) {
                    if (src.getNumberOfPages() == 0) {
                        log.debug("Skipping PDF part with zero pages during merge");
                        continue;
                    }
                    src.copyPagesTo(1, src.getNumberOfPages(), mergedPdf);
                }
            }
        }

        return addPageNumbers(mergedPdfStream.toByteArray());
    }

    private byte[] addPageNumbers(byte[] mergedPdf) throws IOException {
        var pdfOutputStream = new ByteArrayOutputStream();

        try (var pdfDoc = new PdfDocument(
                new PdfReader(new ByteArrayInputStream(mergedPdf)),
                new PdfWriter(pdfOutputStream)
        )) {

            int totalPages = pdfDoc.getNumberOfPages();

            // Create font once, not per page
            var font = PdfFontFactory.createFont(
                    StandardFonts.HELVETICA,
                    PdfEncodings.WINANSI,
                    PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED
            );

            for (int i = 1; i <= totalPages; i++) {
                var page = pdfDoc.getPage(i);
                var pageSize = page.getPageSize();
                var pdfCanvas = new PdfCanvas(
                        page.newContentStreamAfter(), page.getResources(), pdfDoc);

                try (var canvas = new Canvas(pdfCanvas, pageSize)) {
                    canvas.add(new Paragraph(String.format("%s of %s", i, totalPages))
                                       .setFont(font)
                                       .setFontSize(9)
                                       .setFontColor(new DeviceRgb(100, 100, 100))
                                       .setTextAlignment(TextAlignment.CENTER)
                                       .setFixedPosition(0, 20, pageSize.getWidth()));
                }
            }
        }

        return pdfOutputStream.toByteArray();
    }
}
