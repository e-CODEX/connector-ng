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
import eu.ecodex.connector.application.port.api.message.c2ctest.ConnectorSendOutboundTestMessage;
import eu.ecodex.connector.application.port.api.message.c2ctest.ConnectorTestBusinessMessageAS4PropertiesCommand;
import eu.ecodex.connector.application.port.api.message.c2ctest.ConnectorTestBusinessMessageCommand;
import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveMessageReport;
import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveMessageStats;
import eu.ecodex.connector.application.port.api.transport.ConnectorRetrieveTransportStep;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.paging.SortDirection;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReportExportFormat;
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorOutboundMessageDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorMessageDetailDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorMessageDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.transport.ConnectorMessageTransportStepDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.parser.ConnectorRestOutboundMessageParser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.test.ConnectorTestMessageRequest;
import eu.ecodex.connector.infrastructure.outbound.export.ConnectorMessageReportExporterFactory;
import java.io.IOException;
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
    private final ConnectorSendOutboundTestMessage sendTestMessageService;
    private final ConnectorRestOutboundMessageParser restOutboundMessageParser;

    /**
     * Constructs a new instance of ConnectorMessageController.
     *
     * @param listMessagesService          The service for listing messages.
     * @param retrieveMessageService       The service for retrieving a specific message.
     * @param retrieveTransportStepService The service for retrieving a specific transport step.
     * @param retrieveMessageStatsService  The service for retrieving message statistics.
     * @param retrieveMessageReportService The service for retrieving message reports.
     * @param reportExporterFactory        The factory for creating message report exporters.
     * @param sendTestMessageService       The service for sending C2C test message.
     * @param restOutboundMessageParser    The rest outbound message parsing util.
     */
    public ConnectorMessageAdminController(
        ConnectorListMessages listMessagesService,
        ConnectorRetrieveMessage retrieveMessageService,
        ConnectorRetrieveTransportStep retrieveTransportStepService,
        ConnectorRetrieveMessageStats retrieveMessageStatsService,
        ConnectorRetrieveMessageReport retrieveMessageReportService,
        ConnectorMessageReportExporterFactory reportExporterFactory,
        ConnectorSendOutboundTestMessage sendTestMessageService,
        ConnectorRestOutboundMessageParser restOutboundMessageParser) {
        this.listMessagesService = listMessagesService;
        this.retrieveMessageService = retrieveMessageService;
        this.retrieveTransportStepService = retrieveTransportStepService;
        this.retrieveMessageStatsService = retrieveMessageStatsService;
        this.retrieveMessageReportService = retrieveMessageReportService;
        this.reportExporterFactory = reportExporterFactory;
        this.sendTestMessageService = sendTestMessageService;
        this.restOutboundMessageParser = restOutboundMessageParser;
    }

    @Override
    public ConnectorOutboundMessageDto submitOutboundC2CTestMessage(
        ConnectorTestMessageRequest request)
        throws IOException {
        var command = toTestBusinessMessageCommand(request);
        var submittedMessage = sendTestMessageService.execute(command);

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
        String action) {
        var pageRequest = ConnectorPageRequest.of(page, size, "createdAt", SortDirection.DESC);

        var messages = listMessagesService.execute(
            pageRequest,
            identifier,
            backendName,
            direction,
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
    public ConnectorMessageStats getStats(String from, String to, String businessDomain) {
        return retrieveMessageStatsService.execute(from, to, businessDomain);
    }

    @Override
    public ConnectorMessageReportSummary getReports(String from, String to, String businessDomain) {
        return retrieveMessageReportService.execute(from, to, businessDomain);
    }

    @Override
    public ResponseEntity<byte[]> exportReports(
        String from,
        String to,
        String businessDomain,
        ConnectorMessageReportExportFormat format) {
        var reportsSummary = retrieveMessageReportService.execute(from, to, businessDomain);
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
