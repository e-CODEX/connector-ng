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

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.application.service.attachement.ConnectorListAttachmentsService;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorListAttachmentsService")
class ConnectorListAttachmentsServiceTest {
    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;

    @InjectMocks
    private ConnectorListAttachmentsService listAttachmentsService;

    private static ConnectorPageRequest pageRequest(int page, int size) {
        return ConnectorPageRequest.builder().page(page).size(size).build();
    }

    @Nested
    @DisplayName("when retrieving succeeds")
    class WhenRetrievingSucceeds {
        @Test
        void should_return_the_page_provided_by_the_repository() {
            var expected = ConnectorPageResult.of(
                List.of(MessageAttachmentTestFixtures.createAttachment()), 1, 1, 1);
            when(attachmentRepository.findAll(any(), any(), any(), any(), any())).thenReturn(
                expected);

            var result = listAttachmentsService.execute(pageRequest(0, 20), null, null, null, null);

            assertThat(result).isSameAs(expected);
        }

        @Test
        void should_pass_the_page_request_and_filters_to_the_repository() {
            var request = pageRequest(2, 50);
            var messageId = "fake-message-id";
            var name = "fake-name";
            var types = List.of(ConnectorAttachmentType.values());
            var storages = List.of(ConnectorAttachmentStorage.values());
            when(attachmentRepository.findAll(any(), any(), any(), any(), any()))
                .thenReturn(ConnectorPageResult.of(List.of(), 0, 0, 0));

            listAttachmentsService.execute(
                request,
                messageId,
                name,
                types,
                storages
            );

            verify(attachmentRepository)
                .findAll(request, messageId, name, types, storages);
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 20, 100})
        void should_accept_page_sizes_up_to_100(int size) {
            when(attachmentRepository.findAll(any(), any(), any(), any(), any()))
                .thenReturn(ConnectorPageResult.of(List.of(), 0, 0, 0));

            assertThatCode(() -> listAttachmentsService.execute(
                pageRequest(0, size),
                null,
                null,
                null,
                null
            ))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("when the page request is invalid")
    class WhenThePageRequestIsInvalid {
        @Test
        void should_fail_when_the_page_is_negative() {
            assertThatIllegalArgumentException()
                .isThrownBy(() -> listAttachmentsService.execute(
                    pageRequest(-1, 20),
                    null,
                    null,
                    null,
                    null
                ));

            verifyNoInteractions(attachmentRepository);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, 101})
        void should_fail_when_the_size_is_out_of_bounds(int size) {
            assertThrows(
                IllegalArgumentException.class,
                () -> {
                    var request = pageRequest(0, size);
                    listAttachmentsService.execute(request, null, null, null, null);
                }
            );
            verifyNoInteractions(attachmentRepository);
        }

        @Test
        void should_fail_when_the_page_request_is_null() {
            assertThatNullPointerException()
                .isThrownBy(() -> listAttachmentsService.execute(null, null, null, null, null));

            verifyNoInteractions(attachmentRepository);
        }
    }
}
