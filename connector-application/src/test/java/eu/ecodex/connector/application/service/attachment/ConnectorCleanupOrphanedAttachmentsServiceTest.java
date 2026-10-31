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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.application.service.attachement.ConnectorAttachmentFilesDeleter;
import eu.ecodex.connector.application.service.attachement.ConnectorCleanupOrphanedAttachmentsService;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorCleanupOrphanedAttachmentsService")
public class ConnectorCleanupOrphanedAttachmentsServiceTest {
    private static final Instant THRESHOLD = Instant.parse("2026-10-27T22:00:00Z");

    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @Mock
    private ConnectorAttachmentFilesDeleter filesDeleter;

    @InjectMocks
    private ConnectorCleanupOrphanedAttachmentsService service;

    @SuppressWarnings("DataFlowIssue")
    @Nested
    @DisplayName("execute")
    class Execute {
        @Test
        void should_not_call_the_deleter_when_there_are_no_orphans() {
            when(attachmentRepository.findAllOrphaned(THRESHOLD)).thenReturn(List.of());

            service.execute(THRESHOLD);

            verifyNoInteractions(filesDeleter);
        }

        @Test
        void should_delegate_the_orphans_to_the_deleter() {
            var orphans = List.of(mock(ConnectorMessageAttachment.class));
            when(attachmentRepository.findAllOrphaned(THRESHOLD)).thenReturn(orphans);
            when(filesDeleter.delete(orphans))
                .thenReturn(new ConnectorAttachmentFilesDeleter.Result(1, 0));

            service.execute(THRESHOLD);

            verify(filesDeleter).delete(orphans);
        }

        @Test
        void should_throw_when_the_threshold_is_null() {
            assertThatThrownBy(() -> service.execute(null))
                .isInstanceOf(NullPointerException.class);
        }
    }
}
