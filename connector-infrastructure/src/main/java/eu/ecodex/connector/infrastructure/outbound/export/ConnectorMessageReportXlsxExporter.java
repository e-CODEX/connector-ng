/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.export;

import eu.ecodex.connector.application.port.spi.ConnectorMessageReportExporter;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReportExportFormat;
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Exports the connector message report as XLSX.
 */
public class ConnectorMessageReportXlsxExporter implements ConnectorMessageReportExporter {
    private static final String SHEET_NAME = "Connector Message Report";
    private static final String NO_DATA = "No data for the selected period";
    private static final String COUNT_FORMAT = "#,##0";

    private static final List<String> HEADERS = List.of(
        "Party",
        "Service",
        "Inbound",
        "Outbound",
        "Total"
    );

    private static final int COL_PARTY = 0;
    private static final int COL_SERVICE = 1;
    private static final int COL_INBOUND = 2;
    private static final int COL_OUTBOUND = 3;
    private static final int COL_TOTAL = 4;
    private static final int COLUMN_COUNT = 5;

    private static int writeMonthTitle(Sheet sheet, Styles styles, int rowNum, String label) {
        var row = sheet.createRow(rowNum);
        for (var col = 0; col < COLUMN_COUNT; col++) {
            row.createCell(col).setCellStyle(styles.month());
        }
        row.getCell(COL_PARTY).setCellValue(label);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, COL_PARTY, COL_TOTAL));
        return rowNum + 1;
    }

    private static int writeHeader(Sheet sheet, Styles styles, int rowNum) {
        var row = sheet.createRow(rowNum);
        for (var col = 0; col < HEADERS.size(); col++) {
            var cell = row.createCell(col);
            cell.setCellValue(HEADERS.get(col));
            cell.setCellStyle(styles.header());
        }
        return rowNum + 1;
    }

    private static int writeReportLine(
        Sheet sheet, Styles styles, int rowNum,
        String party, String service, long inbound, long outbound, long total) {

        var row = sheet.createRow(rowNum);
        row.createCell(COL_PARTY).setCellValue(party);
        row.createCell(COL_SERVICE).setCellValue(service);
        count(row, COL_INBOUND, inbound, styles.count());
        count(row, COL_OUTBOUND, outbound, styles.count());
        count(row, COL_TOTAL, total, styles.count());
        return rowNum + 1;
    }

    private static int writeTotalLine(
        Sheet sheet, Styles styles, int rowNum,
        long inbound, long outbound, long total) {

        var row = sheet.createRow(rowNum);

        var label = row.createCell(COL_PARTY);
        label.setCellValue("Total");
        label.setCellStyle(styles.total());
        row.createCell(COL_SERVICE).setCellStyle(styles.total());

        count(row, COL_INBOUND, inbound, styles.totalCount());
        count(row, COL_OUTBOUND, outbound, styles.totalCount());
        count(row, COL_TOTAL, total, styles.totalCount());
        return rowNum + 1;
    }

    private static void count(Row row, int column, long value, CellStyle style) {
        var cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void autoSizeColumns(Sheet sheet) {
        for (var col = 0; col < COLUMN_COUNT; col++) {
            sheet.autoSizeColumn(col);
        }
    }

    @Override
    public ConnectorMessageReportExportFormat getFormat() {
        return ConnectorMessageReportExportFormat.XLSX;
    }

    @Override
    public byte[] export(ConnectorMessageReportSummary summary) {
        return write(summary);
    }

    private byte[] write(ConnectorMessageReportSummary summary) {
        try (var workbook = new XSSFWorkbook();
             var out = new ByteArrayOutputStream()) {

            var sheet = workbook.createSheet(SHEET_NAME);
            var styles = Styles.of(workbook);

            var rowNum = 0;
            rowNum = writeHeader(sheet, styles, rowNum);
            rowNum++;

            if (summary.years().isEmpty()) {
                sheet.createRow(rowNum).createCell(COL_PARTY).setCellValue(NO_DATA);
            }

            for (var year : summary.years()) {
                var months = year.months();

                var yearRow = sheet.createRow(rowNum++);
                var yearCell = yearRow.createCell(COL_PARTY);
                yearCell.setCellValue(year.year());
                yearCell.setCellStyle(styles.year());
                yearRow.createCell(COL_TOTAL)
                       .setCellValue(months.size() + (months.size() == 1 ? " month" : " months"));

                rowNum++; // spacer

                for (var month : months) {
                    rowNum = writeMonthTitle(sheet, styles, rowNum, month.label());

                    for (var line : month.reports()) {
                        rowNum = writeReportLine(
                            sheet, styles, rowNum,
                            line.party(), line.service(),
                            line.inbound(), line.outbound(), line.total()
                        );
                    }

                    rowNum = writeTotalLine(
                        sheet, styles, rowNum,
                        month.totalInbound(), month.totalOutbound(), month.total()
                    );

                    rowNum++; // spacer
                }

                rowNum++; // spacer
            }

            autoSizeColumns(sheet);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(
                "Unable to export the connector message report as XLSX",
                e
            );
        }
    }

    private record Styles(
        CellStyle year,
        CellStyle month,
        CellStyle header,
        CellStyle count,
        CellStyle total,
        CellStyle totalCount
    ) {

        static Styles of(Workbook workbook) {
            var bold = workbook.createFont();
            bold.setBold(true);

            var title = workbook.createFont();
            title.setBold(true);
            title.setFontHeightInPoints((short) 14);

            var year = workbook.createCellStyle();
            year.setFont(title);

            var month = workbook.createCellStyle();
            month.setFont(bold);
            month.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            month.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            month.setAlignment(HorizontalAlignment.CENTER);

            var header = workbook.createCellStyle();
            header.setFont(bold);
            header.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            var countFormat = workbook.createDataFormat().getFormat(COUNT_FORMAT);
            var count = workbook.createCellStyle();
            count.setDataFormat(countFormat);

            var total = workbook.createCellStyle();
            total.setFont(bold);
            total.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            total.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            var totalCount = workbook.createCellStyle();
            totalCount.cloneStyleFrom(total);
            totalCount.setDataFormat(countFormat);

            return new Styles(year, month, header, count, total, totalCount);
        }
    }
}
