/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest.controller.admin.message;

import eu.ecodex.connector.application.port.api.message.ConnectorListMessages;
import eu.ecodex.connector.application.port.api.message.ConnectorRetrieveMessage;
import eu.ecodex.connector.application.port.api.message.c2ctest.ConnectorSendOutboundC2CTestMessage;
import eu.ecodex.connector.application.port.api.message.c2ctest.ConnectorTestBusinessMessageAS4PropertiesCommand;
import eu.ecodex.connector.application.port.api.message.c2ctest.ConnectorTestBusinessMessageCommand;
import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveMessageReport;
import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveMessageStats;
import eu.ecodex.connector.application.port.api.transport.ConnectorRetrieveTransportStepByMessageIdentifier;
import eu.ecodex.connector.domain.model.filter.ConnectorDateRangeFilter;
import eu.ecodex.connector.domain.model.filter.ConnectorMessagesListFilter;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.paging.SortDirection;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReportExportFormat;
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.dto.ConnectorOutboundMessageDto;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.dto.message.ConnectorMessageDetailDto;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.dto.message.ConnectorMessageDto;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.dto.transport.ConnectorMessageTransportStepDto;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.parser.ConnectorRestOutboundMessageParser;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.message.c2ctest.ConnectorTestMessageRequest;
import eu.ecodex.connector.infrastructure.adapter.out.export.ConnectorMessageReportExporterFactory;
import java.io.IOException;
import java.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing messages within the connector system.
 */
@RestController
public class ConnectorMessageAdminController implements ConnectorMessageAdminApi {
    private final ConnectorListMessages listMessages;
    private final ConnectorRetrieveMessage retrieveMessage;
    private final ConnectorRetrieveTransportStepByMessageIdentifier retrieveTransportStepService;
    private final ConnectorRetrieveMessageStats retrieveMessageStats;
    private final ConnectorRetrieveMessageReport retrieveMessageReport;
    private final ConnectorMessageReportExporterFactory reportExporterFactory;
    private final ConnectorSendOutboundC2CTestMessage sendOutboundC2CTestMessage;
    private final ConnectorRestOutboundMessageParser restOutboundMessageParser;

    /**
     * Constructs a new instance of ConnectorMessageController.
     *
     * @param listMessages                 The service for listing messages.
     * @param retrieveMessage              The service for retrieving a specific message.
     * @param retrieveTransportStepService The service for retrieving a specific transport step.
     * @param retrieveMessageStats         The service for retrieving message statistics.
     * @param retrieveMessageReport        The service for retrieving message reports.
     * @param reportExporterFactory        The factory for creating message report exporters.
     * @param sendOutboundC2CTestMessage   The service for sending C2C test message.
     * @param restOutboundMessageParser    The rest outbound message parsing util.
     */
    public ConnectorMessageAdminController(
        ConnectorListMessages listMessages,
        ConnectorRetrieveMessage retrieveMessage,
        ConnectorRetrieveTransportStepByMessageIdentifier retrieveTransportStepService,
        ConnectorRetrieveMessageStats retrieveMessageStats,
        ConnectorRetrieveMessageReport retrieveMessageReport,
        ConnectorMessageReportExporterFactory reportExporterFactory,
        ConnectorSendOutboundC2CTestMessage sendOutboundC2CTestMessage,
        ConnectorRestOutboundMessageParser restOutboundMessageParser) {
        this.listMessages = listMessages;
        this.retrieveMessage = retrieveMessage;
        this.retrieveTransportStepService = retrieveTransportStepService;
        this.retrieveMessageStats = retrieveMessageStats;
        this.retrieveMessageReport = retrieveMessageReport;
        this.reportExporterFactory = reportExporterFactory;
        this.sendOutboundC2CTestMessage = sendOutboundC2CTestMessage;
        this.restOutboundMessageParser = restOutboundMessageParser;
    }

    @Override
    public ConnectorOutboundMessageDto submitOutboundC2CTestMessage(
        ConnectorTestMessageRequest request)
        throws IOException {
        var command = toTestBusinessMessageCommand(request);
        var submittedMessage = sendOutboundC2CTestMessage.execute(command);

        return ConnectorOutboundMessageDto.from(submittedMessage);
    }

    @Override
    public ConnectorPageResult<ConnectorMessageDto> listMessages(
        int page,
        int size,
        String identifier,
        String backendName,
        ConnectorMessageDirection direction,
        String businessDomain,
        String service,
        String action,
        Instant from,
        Instant to) {
        var pageRequest = ConnectorPageRequest.of(page, size, "createdAt", SortDirection.DESC);
        var filter = ConnectorMessagesListFilter.of(
            pageRequest,
            identifier,
            backendName,
            direction,
            businessDomain,
            service,
            action,
            ConnectorDateRangeFilter.of(from, to)
        );

        var messages = listMessages.execute(filter);

        return ConnectorPageResult.of(
            messages.content().stream().map(ConnectorMessageDto::from).toList(),
            messages.size(),
            messages.totalElements(),
            messages.totalPages()
        );
    }

    @Override
    public ConnectorMessageDetailDto retrieveMessage(String identifier) {
        var message = retrieveMessage.execute(identifier);

        return ConnectorMessageDetailDto.from(message);
    }

    @Override
    public ConnectorMessageTransportStepDto retrieveMessageTransportStep(String identifier) {
        var step = retrieveTransportStepService.execute(identifier);

        return ConnectorMessageTransportStepDto.from(step);
    }

    @Override
    public ConnectorMessageStats getStats(Instant from, Instant to, String businessDomain) {
        return retrieveMessageStats.execute(ConnectorDateRangeFilter.of(from, to), businessDomain);
    }

    @Override
    public ConnectorMessageReportSummary getReports(
        Instant from,
        Instant to,
        String businessDomain) {
        return retrieveMessageReport.execute(ConnectorDateRangeFilter.of(from, to), businessDomain);
    }

    @Override
    public ResponseEntity<byte[]> exportReports(
        Instant from,
        Instant to,
        String businessDomain,
        ConnectorMessageReportExportFormat format) {
        var reportsSummary = retrieveMessageReport.execute(
            ConnectorDateRangeFilter.of(from, to),
            businessDomain
        );
        var exporter = reportExporterFactory.create(format);
        var export = exporter.export(reportsSummary);

        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType(
                                 exporter.getFormat().getContentType()
                             ))
                             .contentLength(export.length)
                             .header(
                                 HttpHeaders.CONTENT_DISPOSITION,
                                 "attachment; filename=connector-message-report."
                                     + exporter.getFormat().getExtension()
                             )
                             .body(export);
    }

    private ConnectorTestBusinessMessageCommand toTestBusinessMessageCommand(
        ConnectorTestMessageRequest request) throws IOException {
        var requestAs4Properties = request.as4Properties();

        var testAs4PropertiesCommand = ConnectorTestBusinessMessageAS4PropertiesCommand
            .builder()
            .ebmsIdentifier(requestAs4Properties.ebmsIdentifier())
            .conversationIdentifier(requestAs4Properties.conversationIdentifier())
            .originalSender(requestAs4Properties.originalSender())
            .finalRecipient(requestAs4Properties.finalRecipient())
            .fromParty(restOutboundMessageParser.toParty(
                requestAs4Properties.fromParty(),
                ConnectorPartyRoleType.INITIATOR
            ))
            .toParty(restOutboundMessageParser.toParty(
                requestAs4Properties.toParty(),
                ConnectorPartyRoleType.RESPONDER
            ))
            .build();

        return ConnectorTestBusinessMessageCommand
            .builder()
            .businessDomainIdentifier(
                restOutboundMessageParser.resolveBusinessDomainIdentifier(
                    request.businessDomainIdentifier()
                )
            )
            .backendMessageIdentifier(request.backendMessageIdentifier())
            .as4PropertiesCommand(testAs4PropertiesCommand)
            .businessContent(restOutboundMessageParser.toBusinessContent(request.businessContent()))
            .attachments(restOutboundMessageParser.toAttachments(request.attachments()))
            .build();
    }
}
