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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import eu.ecodex.connector.infrastructure.security.exception.ConnectorTrustOKTokenException;
import eu.ecodex.connector.infrastructure.security.token.trustok.pdf.PDFDocumentFonts;
import eu.ecodex.connector.infrastructure.security.token.trustok.pdf.TitleStyle;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

/**
 * Abstract class for creating PDF documents containing technical summaries or other specialized
 * content for connector containers. This class provides utility methods for styling text, adding
 * customized images and stamps, and formatting data to be included in the PDF.
 *
 * <p>Subclasses of this class are expected to implement specific logic for generating
 * complete and customized PDF documents.
 */
public abstract class ConnectorPDFTrustOKSummarySupport {
    private static final String HEADER_IMAGE = "images/eulisa-header.png";
    private static final String SUCCESS_STAMP = "images/green-min.png";
    private static final String FAILURE_STAMP = "images/red-min.png";
    private static final String SUFFICIENT_STAMP = "images/orange-min.png";
    private static final int STAMP_WIDTH = 120;
    private static final int STAMP_HEIGHT = 120;
    private static final String OK_ICON = "icons/ok.jpg";
    private static final String KO_ICON = "icons/error.jpg";
    private static final int STATUS_ICON_SIZE = 9;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm z");

    protected Style ofStyle(TitleStyle titleStyle, PDFDocumentFonts fonts) {
        var style = new Style();
        style.setMarginTop(1);

        switch (titleStyle) {
            case CODE -> {
                style.setFont(fonts.mono());
                style.setFontSize(8);
            }
            case HEADER1 -> {
                style.setFont(fonts.header1());
                style.setFontSize(14);
                style.setMarginTop(10);
                style.setFontColor(new DeviceRgb(54, 95, 145));
            }
            case HEADER2 -> {
                style.setFont(fonts.header2());
                style.setFontSize(13);
                style.setFontColor(new DeviceRgb(79, 129, 189));
            }
            case HEADER3 -> {
                style.setFont(fonts.header3());
                style.setFontSize(12);
                style.setFontColor(new DeviceRgb(79, 129, 189));
            }
            case HEADER4 -> {
                style.setFont(fonts.header4());
                style.setFontSize(11);
                style.setFontColor(new DeviceRgb(79, 129, 189));
            }
            case HEADER5 -> {
                style.setFont(fonts.header5());
                style.setFontSize(10);
                style.setFontColor(new DeviceRgb(79, 129, 189));
            }
            default -> {
                style.setFont(fonts.regular());
                style.setFontSize(9);
            }
        }

        return style;
    }

    private Image createImage(String path) {
        try {
            var resource = new ClassPathResource(path);
            return new Image(ImageDataFactory.create(resource.getURL()));
        } catch (Exception e) {
            throw new ConnectorTrustOKTokenException(
                    "Failed to load image from classpath: " + path, e
            );
        }
    }

    private Image createStatusImage(boolean success) {
        return createImage(success ? OK_ICON : KO_ICON)
                .scaleToFit(STATUS_ICON_SIZE, STATUS_ICON_SIZE);
    }

    private Image createStamp(String path) {
        return createImage(path)
                .setWidth(STAMP_WIDTH)
                .setHeight(STAMP_HEIGHT);
    }

    protected Image createSuccessStamp() {
        return createStamp(SUCCESS_STAMP);
    }

    protected Image createFailureStamp() {
        return createStamp(FAILURE_STAMP);
    }

    protected Image createSufficientStamp() {
        return createStamp(SUFFICIENT_STAMP);
    }

    protected Image generateHeader() {
        return createImage(HEADER_IMAGE)
                .setWidth(UnitValue.createPercentValue(80))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setMarginBottom(40);
    }

    protected Paragraph addParagraph(String text) {
        return new Paragraph(text).setMultipliedLeading(1.0f);
    }

    protected Paragraph addParagraph(String text, TitleStyle style, PDFDocumentFonts fonts) {
        return addParagraph(text)
                .addStyle(ofStyle(style, fonts));
    }

    protected Paragraph addParagraph(
            String text,
            boolean success,
            TitleStyle style,
            PDFDocumentFonts fonts) {
        return addParagraph(text, createStatusImage(success), style, fonts);
    }

    protected Paragraph addParagraph(
            String text,
            Image icon,
            TitleStyle style,
            PDFDocumentFonts fonts) {
        if (style == null) {
            style = TitleStyle.DEFAULT;
        }

        var paragraph = addParagraph("").addStyle(ofStyle(TitleStyle.DEFAULT, fonts));

        if (icon != null) {
            paragraph = paragraph
                    .add(icon)
                    .add(new Text(" ")).addStyle(ofStyle(style, fonts));
        }

        return paragraph.add(new Text(text));
    }

    protected Table createTwoColumnsTable() {
        return createTable(new float[]{1, 1})
                .useAllAvailableWidth()
                .setMarginBottom(20)
                .setFixedLayout();
    }

    protected Table createThreeColumnsTable() {
        return createTable(new float[]{1, 1, 1});
    }

    protected Table createTable(float[] percentArray) {
        return new Table(UnitValue.createPercentArray(percentArray))
                .setBorder(Border.NO_BORDER)
                .setMarginBottom(10)
                .useAllAvailableWidth();
    }

    private Cell createCell(int colSpan, TextAlignment alignment) {
        return new Cell(1, colSpan)
                .setTextAlignment(alignment)
                .setBorder(Border.NO_BORDER);
    }

    protected Cell rightCell(String text) {
        return createCell(1, TextAlignment.RIGHT)
                .add(new Paragraph(text).setTextAlignment(TextAlignment.RIGHT));
    }

    protected Cell centerCell(String text) {
        return createCell(2, TextAlignment.CENTER)
                .add(new Paragraph(text).setTextAlignment(TextAlignment.CENTER));
    }

    protected Cell leftCell(String text) {
        return createCell(2, TextAlignment.LEFT)
                .add(new Paragraph(text).setTextAlignment(TextAlignment.LEFT));
    }

    protected Paragraph addTitle(String title, PDFDocumentFonts fonts) {
        return addParagraph(title)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12)
                .setFont(fonts.bold());
    }

    protected String getTextOrNA(String text) {
        if (!StringUtils.hasText(text)) {
            return "N/A";
        }

        return text;
    }

    protected String formatDateOrNA(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return "N/A";
        }

        var zonedDateTime = calendar.toGregorianCalendar().toZonedDateTime();

        return DATE_TIME_FORMATTER.format(zonedDateTime);
    }

    protected String formatDateOrNA(Date date) {
        if (date == null) {
            return "N/A";
        }

        return DATE_TIME_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()));
    }
}
