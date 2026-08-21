/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.attachment;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.application.service.attachement.ConnectorListAttachmentsService;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorListAttachmentsService")
public class ConnectorListAttachmentsServiceTest {
    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;

    @InjectMocks
    private ConnectorListAttachmentsService connectorListAttachmentsService;

    @Nested
    @DisplayName("when retrieving succeeds")
    class WhenRetrievingSucceeds {
        @Test
        void should_return_the_paged_attachments() {
            var pageResult = ConnectorPageResult.of(
                List.of(MessageAttachmentTestFixtures.createAttachment()), 1, 1, 1
            );
            when(attachmentRepository.findAll(any())).thenReturn(pageResult);

            var pageRequest = ConnectorPageRequest.builder().page(0).size(20).build();
            var result = connectorListAttachmentsService.execute(pageRequest);

            assertThat(result).isNotNull();
            assertThat(result.content()).isNotEmpty();
            assertThat(result.totalElements()).isEqualTo(1L);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("when the page request is invalid")
    class WhenThePageRequestIsInvalid {
        @Test
        void should_fail_when_the_page_is_negative() {
            assertThrows(
                IllegalArgumentException.class,
                () -> {
                    var pageRequest = ConnectorPageRequest.builder().page(-1).size(20).build();
                    connectorListAttachmentsService.execute(pageRequest);
                }
            );
        }

        @Test
        void should_fail_when_the_size_exceeds_100() {
            assertThrows(
                IllegalArgumentException.class,
                () -> {
                    var pageRequest = ConnectorPageRequest.builder().page(0).size(101).build();
                    connectorListAttachmentsService.execute(pageRequest);
                }
            );
        }

        @Test
        void should_fail_when_the_page_request_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> connectorListAttachmentsService.execute(null)
            );
        }
    }
}
