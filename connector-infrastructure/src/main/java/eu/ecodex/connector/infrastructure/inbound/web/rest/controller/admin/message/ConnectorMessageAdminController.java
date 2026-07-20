/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.message;

import eu.ecodex.connector.application.port.api.message.ConnectorListMessages;
import eu.ecodex.connector.application.port.api.message.ConnectorRetrieveMessage;
import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveMessageReport;
import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveMessageStats;
import eu.ecodex.connector.application.port.api.transport.ConnectorRetrieveTransportStep;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.paging.SortDirection;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReportExportFormat;
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorMessageDetailDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorMessageDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.transport.ConnectorMessageTransportStepDto;
import eu.ecodex.connector.infrastructure.outbound.export.ConnectorMessageReportExporterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing messages within the connector system.
 */
@RestController
public class ConnectorMessageAdminController implements ConnectorMessageAdminApi {
    private final ConnectorListMessages listMessagesService;
    private final ConnectorRetrieveMessage retrieveMessageService;
    private final ConnectorRetrieveTransportStep retrieveTransportStepService;
    private final ConnectorRetrieveMessageStats retrieveMessageStatsService;
    private final ConnectorRetrieveMessageReport retrieveMessageReportService;
    private final ConnectorMessageReportExporterFactory reportExporterFactory;

    /**
     * Constructs a new instance of ConnectorMessageController.
     *
     * @param listMessagesService          The service for listing messages.
     * @param retrieveMessageService       The service for retrieving a specific message.
     * @param retrieveTransportStepService The service for retrieving a specific transport step.
     * @param retrieveMessageStatsService  The service for retrieving message statistics.
     * @param retrieveMessageReportService The service for retrieving message reports.
     * @param reportExporterFactory        The factory for creating message report exporters.
     */
    public ConnectorMessageAdminController(
        ConnectorListMessages listMessagesService,
        ConnectorRetrieveMessage retrieveMessageService,
        ConnectorRetrieveTransportStep retrieveTransportStepService,
        ConnectorRetrieveMessageStats retrieveMessageStatsService,
        ConnectorRetrieveMessageReport retrieveMessageReportService,
        ConnectorMessageReportExporterFactory reportExporterFactory) {
        this.listMessagesService = listMessagesService;
        this.retrieveMessageService = retrieveMessageService;
        this.retrieveTransportStepService = retrieveTransportStepService;
        this.retrieveMessageStatsService = retrieveMessageStatsService;
        this.retrieveMessageReportService = retrieveMessageReportService;
        this.reportExporterFactory = reportExporterFactory;
    }

    @Override
    public ConnectorPageResult<ConnectorMessageDto> listMessages(
        int page,
        int size,
        String identifier,
        String backendName,
        String businessDomain,
        String service,
        String action) {
        var pageRequest = ConnectorPageRequest.of(page, size, "createdAt", SortDirection.DESC);

        var messages = listMessagesService.execute(
            pageRequest,
            identifier,
            backendName,
            businessDomain,
            service,
            action
        );

        return ConnectorPageResult.of(
            messages.content().stream().map(ConnectorMessageDto::from).toList(),
            messages.size(),
            messages.totalElements(),
            messages.totalPages()
        );
    }

    @Override
    public ConnectorMessageDetailDto retrieveMessage(String identifier) {
        var message = retrieveMessageService.execute(identifier);

        return ConnectorMessageDetailDto.from(message);
    }

    @Override
    public ConnectorMessageTransportStepDto retrieveMessageTransportStep(String identifier) {
        var step = retrieveTransportStepService.execute(identifier);

        return ConnectorMessageTransportStepDto.from(step);
    }

    @Override
    public ConnectorMessageStats getStats(String from, String to) {
        return retrieveMessageStatsService.execute(from, to);
    }

    @Override
    public ConnectorMessageReportSummary getReports(String from, String to) {
        return retrieveMessageReportService.execute(from, to);
    }

    @Override
    public ResponseEntity<byte[]> exportReports(
        String from,
        String to,
        ConnectorMessageReportExportFormat format) {
        var reportsSummary = retrieveMessageReportService.execute(from, to);
        var exporter = reportExporterFactory.create(format);

        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType(
                                 exporter.getFormat().getContentType()
                             ))
                             .header(
                                 HttpHeaders.CONTENT_DISPOSITION,
                                 "attachment; filename=connector-message-report."
                                     + exporter.getFormat().getExtension()
                             )
                             .body(exporter.export(reportsSummary));
    }
}
