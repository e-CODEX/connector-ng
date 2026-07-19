/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.token.trustok.pdf.summary;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import eu.ecodex.connector.infrastructure.outbound.security.exception.ConnectorTrustOKTokenException;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenLegalTrustLevel;
import eu.ecodex.connector.infrastructure.outbound.security.token.trustok.pdf.PDFDocumentFonts;
import java.io.ByteArrayOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * The ConnectorContainerPdfLegalSummaryGenerator class is responsible for generating PDF documents
 * that summarize the legal status and validation of a given {@link ConnectorToken}.
 */
@Slf4j
@Component
public class ConnectorPDFTrustOKLegalSummaryGenerator extends ConnectorPDFTrustOKSummarySupport
    implements ConnectorPDFTrustOKSummaryGenerator {
    private static final String DISCLAIMER =
        "The Advanced Electronic System (AES) assures the recipient of this message that the "
            + "initiator of the message is representing the sending authority. Origin of the "
            + "message and initiator's actions can be traced back in the log files of the Case "
            + "Management System (AES) of the sending authority.";

    private static final String FURTHER_DETAILS =
        "Further details can be found in the attached validation report and its technical "
            + "assessment.";

    private static final DeviceRgb YELLOW_BORDER = new DeviceRgb(255, 255, 0);
    private static final DeviceRgb BLUE_BACKGROUND = new DeviceRgb(157, 206, 237);

    @Override
    public ByteArrayOutputStream generate(ConnectorToken token) {
        log.debug("Creating TrustOK PDF legal summary");

        var pdfFonts = PDFDocumentFonts.createFonts();
        var outputStream = new ByteArrayOutputStream();
        var writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_1_7);

        try (
            var writer = new PdfWriter(outputStream, writerProperties);
            var pdfDocument = new PdfDocument(writer);
            var document = new Document(pdfDocument)
        ) {
            document.setFontSize(10);
            addContent(document, token, pdfFonts);
        } catch (Exception e) {
            log.error("Failed to generate TrustOK PDF legal summary");
            throw new ConnectorTrustOKTokenException(
                "Failed to generate TrustOK PDF legal summary", e
            );
        }

        return outputStream;
    }

    private void addContent(Document document, ConnectorToken token, PDFDocumentFonts pdfFonts) {
        document.add(generateHeader());
        document.add(addTitle("e-Justice Communication via Online Data Exchange", pdfFonts)
                         .setMarginBottom(60));
        document.add(buildTrustOkTokenBanner());
        document.add(addGeneralInfo(token));
        document.add(addLegalResultInfo(token));
        document.add(addLegalValidationInfo(token));
        document.add(buildDisclaimerParagraph());
    }

    private Paragraph buildDisclaimerParagraph() {
        return addParagraph(DISCLAIMER)
            .setFontSize(10)
            .setPadding(1)
            .setTextAlignment(TextAlignment.CENTER)
            .setBorder(new SolidBorder(ColorConstants.BLACK, 1));
    }

    private Paragraph buildTrustOkTokenBanner() {
        return addParagraph("Trust OK-Token")
            .setFontSize(20)
            .setPadding(2)
            .setBorder(new SolidBorder(YELLOW_BORDER, 2))
            .setBackgroundColor(BLUE_BACKGROUND)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
    }

    private Table addGeneralInfo(ConnectorToken token) {
        var info = createTwoColumnsTable();

        info.addCell(leftCell("General Information"));

        var issuer = token.getIssuer();

        var country = issuer.getCountry();
        info.addCell(rightCell("Issuing Country"));
        info.addCell(rightCell(getTextOrNA(country)));

        var electronicSystem = issuer.getAdvancedElectronicSystem();
        info.addCell(rightCell("Advanced Electronic System"));
        info.addCell(rightCell(getTextOrNA(electronicSystem.getText())));

        var documentType = getTextOrNA(token.getDocument().getType());
        var documentName = getTextOrNA(token.getDocument().getFilename());
        var documentInfo = String.format("%s, %s", documentType, documentName);
        info.addCell(rightCell("Document Information"));
        info.addCell(rightCell(documentInfo));

        var verificationTime = formatDateOrNA(token.getValidationVerificationTime());
        info.addCell(rightCell("Time of Issuance"));
        info.addCell(rightCell(verificationTime));

        return info;
    }

    private Table addLegalResultInfo(ConnectorToken token) {
        var info = createTwoColumnsTable();
        info.addCell(leftCell("Legal Result"));
        info.addCell(rightCell("Evaluation of the Document"));

        var trustLevel = token.getLegalValidationResultTrustLevel();
        info.addCell(rightCell(getTextOrNA(trustLevel == null ? null : trustLevel.getText())))
            .setTextAlignment(TextAlignment.RIGHT);

        return info;
    }

    private Table addLegalValidationInfo(ConnectorToken token) {
        var info = createThreeColumnsTable()
            .setBackgroundColor(BLUE_BACKGROUND)
            .setBorder(new SolidBorder(YELLOW_BORDER, 2));

        var trustLevel = token.getLegalValidationResultTrustLevel();
        final Image trustStamp;

        if (trustLevel == ConnectorTokenLegalTrustLevel.SUCCESSFUL) {
            trustStamp = createSuccessStamp();
        } else {
            trustStamp = createFailureStamp();
        }

        trustStamp.setWidth(100).setHeight(100);

        var disclaimer = token.getLegalValidationResultDisclaimer();
        info.addCell(new Cell(1, 2).add(addParagraph(disclaimer).setMarginBottom(15))
                                   .add(addParagraph(FURTHER_DETAILS))
                                   .setBorderRight(Border.NO_BORDER)
                                   .setPadding(10)
        );
        info.addCell(new Cell().add(trustStamp.setHorizontalAlignment(HorizontalAlignment.RIGHT))
                               .setPadding(10)
                               .setTextAlignment(TextAlignment.RIGHT)
                               .setBorderLeft(Border.NO_BORDER)
        );

        return info;
    }
}
