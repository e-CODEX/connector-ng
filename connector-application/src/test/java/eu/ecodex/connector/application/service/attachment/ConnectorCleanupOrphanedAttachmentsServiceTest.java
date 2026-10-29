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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.application.service.attachement.ConnectorCleanupOrphanedAttachmentsService;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("")
public class ConnectorCleanupOrphanedAttachmentsServiceTest {
    private static final Instant NOW = Instant.parse("2026-10-28T22:00:00Z");
    private static final Instant THRESHOLD = NOW.minus(Duration.ofDays(1));

    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;

    @Mock
    private ConnectorFileStorageProvider fileStorageProvider;

    private ConnectorCleanupOrphanedAttachmentsService service;

    @BeforeEach
    void setUp() {
        var fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new ConnectorCleanupOrphanedAttachmentsService(
            attachmentRepository, fileStorageProvider, fixedClock);
    }

    private ConnectorMessageAttachment attachment(String identifier) {
        return ConnectorMessageAttachment.builder()
                                         .identifier(identifier)
                                         .build();
    }

    @Nested
    @DisplayName("when there are no orphans")
    class WhenThereAreNoOrphans {
        @Test
        void does_not_touch_the_storage_nor_mark_anything_as_deleted() {
            when(attachmentRepository.findAllOrphaned(THRESHOLD)).thenReturn(List.of());

            service.execute(THRESHOLD);

            verifyNoInteractions(fileStorageProvider);
            verify(attachmentRepository, never()).markAsDeleted(anyList(), any());
        }
    }

    @Nested
    @DisplayName("when all deletions succeed")
    class WhenAllDeletionsSucceed {
        @Test
        void deletes_every_file_from_the_storage() {
            when(attachmentRepository.findAllOrphaned(THRESHOLD))
                .thenReturn(List.of(attachment("id-1"), attachment("id-2")));
            doNothing().when(attachmentRepository).markAsDeleted(anyList(), any());

            service.execute(THRESHOLD);

            verify(fileStorageProvider).deleteByIdentifier("id-1");
            verify(fileStorageProvider).deleteByIdentifier("id-2");
        }

        @Test
        void marks_all_as_deleted_with_the_clock_instant() {
            when(attachmentRepository.findAllOrphaned(THRESHOLD))
                .thenReturn(List.of(attachment("id-1"), attachment("id-2")));

            service.execute(THRESHOLD);

            verify(attachmentRepository).markAsDeleted(List.of("id-1", "id-2"), NOW);
        }
    }

    @Nested
    @DisplayName("when one deletion fails")
    class WhenOneDeletionFails {
        @BeforeEach
        void givenId2Fails() {
            when(attachmentRepository.findAllOrphaned(THRESHOLD))
                .thenReturn(List.of(attachment("id-1"), attachment("id-2"), attachment("id-3")));
            lenient().doThrow(new RuntimeException("S3 unavailable"))
                     .when(fileStorageProvider).deleteByIdentifier("id-2");
        }

        @Test
        void continues_with_the_remaining_attachments() {
            service.execute(THRESHOLD);

            verify(fileStorageProvider).deleteByIdentifier("id-1");
            verify(fileStorageProvider).deleteByIdentifier("id-2");
            verify(fileStorageProvider).deleteByIdentifier("id-3");
        }

        @Test
        void marks_only_the_successful_ones_as_deleted_so_the_failed_one_is_retried() {
            service.execute(THRESHOLD);

            verify(attachmentRepository).markAsDeleted(List.of("id-1", "id-3"), NOW);
        }
    }

    @Nested
    @DisplayName("when all deletions fail")
    class WhenAllDeletionsFail {
        @Test
        void does_not_mark_anything_as_deleted() {
            when(attachmentRepository.findAllOrphaned(THRESHOLD))
                .thenReturn(List.of(attachment("id-1"), attachment("id-2")));
            doThrow(new RuntimeException("S3 unavailable"))
                .when(fileStorageProvider).deleteByIdentifier(anyString());

            service.execute(THRESHOLD);

            verify(attachmentRepository, never()).markAsDeleted(anyList(), any());
        }
    }

    @Nested
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("when the threshold is null")
    class WhenTheThresholdIsNull {
        @Test
        void throws_a_null_pointer_exception_and_does_nothing() {
            assertThatThrownBy(() -> service.execute(null))
                .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(attachmentRepository, fileStorageProvider);
        }
    }
}
