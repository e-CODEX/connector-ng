/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.MessageReportTestFixtures;
import eu.ecodex.connector.MessageStatsTestFixtures;
import eu.ecodex.connector.TransportStepFixtures;
import eu.ecodex.connector.application.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.application.port.api.message.ConnectorListMessages;
import eu.ecodex.connector.application.port.api.message.ConnectorRetrieveMessage;
import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveMessageReport;
import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveMessageStats;
import eu.ecodex.connector.application.port.api.transport.ConnectorRetrieveTransportStep;
import eu.ecodex.connector.application.port.spi.ConnectorMessageReportExporter;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReportExportFormat;
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.message.ConnectorMessageAdminController;
import eu.ecodex.connector.infrastructure.outbound.export.ConnectorMessageReportExporterFactory;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorMessageAdminController")
@WebMvcTest(ConnectorMessageAdminController.class)
public class ConnectorMessageAdminControllerTest extends AbstractWebMvcTest {
    private static final String URL = "/api/v1/admin/messages";
    private static final String URL_STATS = "/api/v1/admin/messages/stats";
    private static final String URL_REPORT = "/api/v1/admin/messages/reports";
    private static final String URL_REPORT_EXPORT = "/api/v1/admin/messages/reports/export?format"
        + "=%s";
    private static final String URL_MESSAGE_DETAIL = "/api/v1/admin/messages/%s";
    private static final String URL_TRANSPORT_STEP = "/api/v1/admin/messages/%s/transport-steps";

    private static final String MESSAGE_ID =
        "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu";

    @MockitoBean
    private ConnectorListMessages listMessagesService;
    @MockitoBean
    private ConnectorRetrieveMessage retrieveMessageService;
    @MockitoBean
    private ConnectorRetrieveTransportStep retrieveTransportStepService;
    @MockitoBean
    private ConnectorRetrieveMessageStats retrieveMessageStatsService;
    @MockitoBean
    private ConnectorRetrieveMessageReport retrieveMessageReportService;
    @MockitoBean
    private ConnectorMessageReportExporterFactory reportExporterFactory;
    @Mock
    private ConnectorMessageReportExporter csvExporter;
    @Mock
    private ConnectorMessageReportExporter jsonExporter;
    @Mock
    private ConnectorMessageReportExporter xlsxExporter;
    @Autowired
    private MockMvc mockMvc;

    private ConnectorMessageReportExporter exporterFor(ConnectorMessageReportExportFormat format) {
        return switch (format) {
            case CSV -> csvExporter;
            case JSON -> jsonExporter;
            case XLSX -> xlsxExporter;
        };
    }

    @Nested
    @DisplayName("GET (list messages)")
    class ListMessages {
        @Test
        void should_return_200_with_the_paged_messages() throws Exception {
            var pageResult = new ConnectorPageResult<>(
                List.of(BusinessMessageTestFixtures.createConfirmedMessage()), 1, 1, 1
            );

            when(listMessagesService.execute(any(), any(), any(), any(), any(), any()))
                .thenReturn(pageResult);

            mockMvc.perform(get(URL)
                                .param("page", "0")
                                .param("size", "20")
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.totalElements").value(1))
                   .andExpect(jsonPath("$.totalPages").value(1))
                   .andExpect(jsonPath("$.size").value(1))
                   .andExpect(jsonPath("$.content").isArray())
                   .andExpect(jsonPath("$.content.length()").value(1));
        }
    }

    @Nested
    @DisplayName("GET (retrieve a message)")
    class RetrieveMessage {
        @Test
        void should_return_200_with_the_message() throws Exception {
            when(retrieveMessageService.execute(any()))
                .thenReturn(BusinessMessageTestFixtures.createConfirmedMessage());

            mockMvc.perform(get(URL_MESSAGE_DETAIL.formatted(MESSAGE_ID))
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.identifier").value(MESSAGE_ID));
        }

        @Test
        void should_return_404_when_the_message_is_not_found() throws Exception {
            doThrow(ConnectorMessageNotFoundException.class)
                .when(retrieveMessageService).execute(any());

            mockMvc.perform(get(URL_MESSAGE_DETAIL.formatted("unknown-identifier"))
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isNotFound())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("GET (retrieve a message's transport steps)")
    class RetrieveTransportSteps {
        @Test
        void should_return_200_with_the_transport_steps() throws Exception {
            when(retrieveTransportStepService.execute(any()))
                .thenReturn(TransportStepFixtures.createTransportStep());

            mockMvc.perform(get(URL_TRANSPORT_STEP.formatted(MESSAGE_ID))
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.transportedMessageIdentifier").value(MESSAGE_ID));
        }

        @Test
        void should_return_404_when_the_message_is_not_found() throws Exception {
            doThrow(ConnectorMessageTransportStepNotFoundException.class)
                .when(retrieveTransportStepService).execute(any());

            mockMvc.perform(get(URL_TRANSPORT_STEP.formatted("unknown-identifier"))
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isNotFound())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("GET (message stats)")
    class MessageStats {
        @Test
        void should_return_200_with_the_message_stats() throws Exception {
            when(retrieveMessageStatsService.execute(any(), any(), any()))
                .thenReturn(MessageStatsTestFixtures.createStats());

            mockMvc.perform(get(URL_STATS).contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.all.total").value(100))
                   .andExpect(jsonPath("$.all.delivered").value(90))
                   .andExpect(jsonPath("$.all.rejected").value(10))
                   .andExpect(jsonPath("$.all.pending").value(0))
                   .andExpect(jsonPath("$.outbound.total").value(80))
                   .andExpect(jsonPath("$.outbound.delivered").value(75))
                   .andExpect(jsonPath("$.outbound.rejected").value(5))
                   .andExpect(jsonPath("$.outbound.pending").value(0))
                   .andExpect(jsonPath("$.inbound.total").value(20))
                   .andExpect(jsonPath("$.inbound.delivered").value(15))
                   .andExpect(jsonPath("$.inbound.rejected").value(5))
                   .andExpect(jsonPath("$.inbound.pending").value(0));
        }
    }

    @Nested
    @DisplayName("GET (message report)")
    class MessageReport {
        @Test
        void should_return_200_with_the_message_report() throws Exception {
            when(retrieveMessageReportService.execute(any(), any(), any()))
                .thenReturn(ConnectorMessageReportSummary.of(MessageReportTestFixtures.createReport()));

            mockMvc.perform(get(URL_REPORT).contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.services").isArray())
                   .andExpect(jsonPath("$.services").isNotEmpty())
                   .andExpect(jsonPath("$.services[0]").value("EPO"))
                   .andExpect(jsonPath("$.services[1]").value("FP"))
                   .andExpect(jsonPath("$.parties").isArray())
                   .andExpect(jsonPath("$.parties").isNotEmpty())
                   .andExpect(jsonPath("$.parties[0]").value("RE"))
                   .andExpect(jsonPath("$.months").isArray())
                   .andExpect(jsonPath("$.months").isNotEmpty())
                   .andExpect(jsonPath("$.months[0].year").value(2026))
                   .andExpect(jsonPath("$.months[0].month").value(5))
                   .andExpect(jsonPath("$.months[1].year").value(2026))
                   .andExpect(jsonPath("$.months[1].month").value(6))
                   .andExpect(jsonPath("$.years").isArray())
                   .andExpect(jsonPath("$.years").isNotEmpty())
                   .andExpect(jsonPath("$.years[0].year").value(2026))
                   .andExpect(jsonPath("$.years[0].months").isArray())
                   .andExpect(jsonPath("$.years[0].months").isNotEmpty())
                   .andExpect(jsonPath("$.years[0].months[0].month").value(5))
                   .andExpect(jsonPath("$.years[0].months[0].label").value("May"))
                   .andExpect(jsonPath("$.years[0].months[0].totalInbound").value(1))
                   .andExpect(jsonPath("$.years[0].months[0].totalOutbound").value(0))
                   .andExpect(jsonPath("$.years[0].months[0].total").value(1))
                   .andExpect(jsonPath("$.years[0].months[0].reports[0].party").value("RE"))
                   .andExpect(jsonPath("$.years[0].months[0].reports[0].service").value("FP"))
                   .andExpect(jsonPath("$.years[0].months[0].reports[0].inbound").value(1))
                   .andExpect(jsonPath("$.years[0].months[0].reports[0].outbound").value(0))
                   .andExpect(jsonPath("$.years[0].months[0].reports[0].total").value(1))
                   .andExpect(jsonPath("$.years[0].months[1].month").value(6))
                   .andExpect(jsonPath("$.years[0].months[1].label").value("June"))
                   .andExpect(jsonPath("$.years[0].months[1].totalInbound").value(0))
                   .andExpect(jsonPath("$.years[0].months[1].totalOutbound").value(1))
                   .andExpect(jsonPath("$.years[0].months[1].total").value(1))
                   .andExpect(jsonPath("$.years[0].months[1].reports[0].party").value("RE"))
                   .andExpect(jsonPath("$.years[0].months[1].reports[0].service").value("EPO"))
                   .andExpect(jsonPath("$.years[0].months[1].reports[0].inbound").value(0))
                   .andExpect(jsonPath("$.years[0].months[1].reports[0].outbound").value(1))
                   .andExpect(jsonPath("$.years[0].months[1].reports[0].total").value(1));
        }
    }

    @Nested
    @DisplayName("GET (export the message report)")
    class ExportReport {
        @ParameterizedTest
        @EnumSource(ConnectorMessageReportExportFormat.class)
        void should_export_the_report_in_the_requested_format(
            ConnectorMessageReportExportFormat format) throws Exception {
            var exporter = exporterFor(format);

            when(retrieveMessageReportService.execute(any(), any(), any()))
                .thenReturn(ConnectorMessageReportSummary.of(MessageReportTestFixtures.createReport()));
            when(reportExporterFactory.create(format)).thenReturn(exporter);
            when(exporter.export(any())).thenReturn("dummy-content".getBytes(StandardCharsets.UTF_8));
            when(exporter.getFormat()).thenReturn(format);

            mockMvc.perform(get(URL_REPORT_EXPORT.formatted(format))
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.valueOf(format.getContentType())));

            verify(reportExporterFactory).create(format);
            verify(exporter).export(any());
        }
    }
}
