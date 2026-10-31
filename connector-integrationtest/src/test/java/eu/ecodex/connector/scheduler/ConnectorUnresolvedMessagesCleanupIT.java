/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import eu.ecodex.connector.application.exception.attachment.ConnectorMessageAttachmentException;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.infrastructure.adapter.in.scheduler.ConnectorUnresolvedMessagesCleanupJob;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@TestPropertySource(properties = {
    "connector.scheduling.enabled=false",
    "connector.file-deletion.unresolved-messages.threshold=1d"
})
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id IS NOT NULL",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
    scripts = {
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/processing-mode-party.sql",
        "classpath:sql/processing-mode-service.sql",
        "classpath:sql/processing-mode-action.sql",
        "classpath:sql/message.sql",
        "classpath:sql/message-as4-properties.sql",
        "classpath:sql/attachment.sql",
        "classpath:sql/message-business-content.sql",
        "classpath:sql/message-business-document.sql",
        "classpath:sql/evidence.sql",
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DisplayName("ConnectorUnresolvedMessagesCleanupIT CRON")
public class ConnectorUnresolvedMessagesCleanupIT extends AbstractSchedulerTest {
    private static final String MESSAGE_1 =
        "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu";
    private static final String MESSAGE_2 =
        "7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu";
    private static final String MESSAGE_3 =
        "7a169fa8-1f0d-4a2c-aade-796b0b02fe58@connector.ecodex.eu";
    private static final String MESSAGE_4 =
        "3fae4358-7cc9-4929-a17b-4432cbb8b9cc@connector.ecodex.eu";

    private static final String ORPHAN_ATTACHMENT =
        "d98a621a-4d14-4cfb-be00-0feae9f9b277_fake_file";
    private static final String ATTACHMENT_OF_MESSAGE_3 =
        "b9bd63c7-5a5a-4701-a23c-e5e714436585_messageContent";

    @Autowired
    private ConnectorUnresolvedMessagesCleanupJob job;
    @Autowired
    private ConnectorMessageRepository messageRepository;

    private int totalAttachmentsOf(String messageIdentifier) {
        return jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*) FROM connector_message_attachments a
                  JOIN connector_messages m ON a.message_id = m.id
                 WHERE m.identifier = ?
                """,
            Integer.class,
            messageIdentifier
        );
    }

    private int deletedAttachmentsOf(String messageIdentifier) {
        return jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*) FROM connector_message_attachments a
                  JOIN connector_messages m ON a.message_id = m.id
                 WHERE m.identifier = ? AND a.deleted_at IS NOT NULL
                """,
            Integer.class,
            messageIdentifier
        );
    }

    private boolean isDeleted(String attachmentIdentifier) {
        return jdbcTemplate.queryForObject(
            "SELECT deleted_at IS NOT NULL FROM connector_message_attachments "
                + "WHERE identifier = ?",
            Boolean.class,
            attachmentIdentifier
        );
    }

    private boolean isMessageDeleted(String messageIdentifier) {
        return jdbcTemplate.queryForObject(
            "SELECT deleted_at IS NOT NULL FROM connector_messages WHERE identifier = ?",
            Boolean.class,
            messageIdentifier
        );
    }

    private Timestamp messageDeletedAt(String messageIdentifier) {
        return jdbcTemplate.queryForObject(
            "SELECT deleted_at FROM connector_messages WHERE identifier = ?",
            Timestamp.class,
            messageIdentifier
        );
    }

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    void should_mark_the_attachments_of_every_unresolved_message_as_deleted() {
        job.run();

        for (var message : new String[]{MESSAGE_1, MESSAGE_2, MESSAGE_3, MESSAGE_4}) {
            assertThat(totalAttachmentsOf(message)).isPositive();
            assertThat(deletedAttachmentsOf(message)).isEqualTo(totalAttachmentsOf(message));
        }
    }

    @Test
    void should_set_deleted_at_on_every_unresolved_message() {
        job.run();

        for (var message : new String[]{MESSAGE_1, MESSAGE_2, MESSAGE_3, MESSAGE_4}) {
            assertThat(isMessageDeleted(message)).isTrue();
        }
    }

    @Test
    void should_delete_the_content_of_the_attachments_from_the_storage() {
        job.run();

        verify(fileStorageProvider).deleteByIdentifier(ATTACHMENT_OF_MESSAGE_3);
    }

    @Test
    void should_not_touch_the_attachments_of_a_confirmed_message() {
        messageRepository.setAsConfirmed(MESSAGE_1);

        job.run();

        assertThat(deletedAttachmentsOf(MESSAGE_1)).isZero();
        assertThat(deletedAttachmentsOf(MESSAGE_2)).isEqualTo(totalAttachmentsOf(MESSAGE_2));
    }

    @Test
    void should_not_set_deleted_at_on_a_confirmed_message() {
        messageRepository.setAsConfirmed(MESSAGE_1);

        job.run();

        assertThat(isMessageDeleted(MESSAGE_1)).isFalse();
        assertThat(isMessageDeleted(MESSAGE_2)).isTrue();
    }

    @Test
    void should_not_touch_the_attachments_of_a_rejected_message() {
        messageRepository.setAsRejected(MESSAGE_2);

        job.run();

        assertThat(deletedAttachmentsOf(MESSAGE_2)).isZero();
        assertThat(deletedAttachmentsOf(MESSAGE_1)).isEqualTo(totalAttachmentsOf(MESSAGE_1));
    }

    @Test
    void should_not_set_deleted_at_on_a_rejected_message() {
        messageRepository.setAsRejected(MESSAGE_2);

        job.run();

        assertThat(isMessageDeleted(MESSAGE_2)).isFalse();
        assertThat(isMessageDeleted(MESSAGE_1)).isTrue();
    }

    @Test
    void should_not_touch_the_orphan_attachments() {
        job.run();

        assertThat(isDeleted(ORPHAN_ATTACHMENT)).isFalse();
        verify(fileStorageProvider, never()).deleteByIdentifier(ORPHAN_ATTACHMENT);
    }

    @Test
    void should_keep_an_attachment_active_when_its_content_could_not_be_deleted() {
        doThrow(new ConnectorMessageAttachmentException("storage error"))
            .when(fileStorageProvider).deleteByIdentifier(ATTACHMENT_OF_MESSAGE_3);

        job.run();

        assertThat(isDeleted(ATTACHMENT_OF_MESSAGE_3)).isFalse();
        assertThat(deletedAttachmentsOf(MESSAGE_3)).isEqualTo(totalAttachmentsOf(MESSAGE_3) - 1);
        assertThat(deletedAttachmentsOf(MESSAGE_4)).isEqualTo(totalAttachmentsOf(MESSAGE_4));
    }

    @Test
    void should_not_set_deleted_at_on_a_message_whose_attachment_could_not_be_deleted() {
        doThrow(new ConnectorMessageAttachmentException("storage error"))
            .when(fileStorageProvider).deleteByIdentifier(ATTACHMENT_OF_MESSAGE_3);

        job.run();

        assertThat(isMessageDeleted(MESSAGE_3)).isFalse();   // retried at the next run
        assertThat(isMessageDeleted(MESSAGE_4)).isTrue();    // other messages are not affected
    }

    @Test
    void should_set_deleted_at_on_the_next_run_once_the_storage_works_again() {
        doThrow(new ConnectorMessageAttachmentException("storage error"))
            .when(fileStorageProvider).deleteByIdentifier(ATTACHMENT_OF_MESSAGE_3);
        job.run();
        reset(fileStorageProvider);

        job.run();

        assertThat(isMessageDeleted(MESSAGE_3)).isTrue();
        assertThat(deletedAttachmentsOf(MESSAGE_3)).isEqualTo(totalAttachmentsOf(MESSAGE_3));
    }

    @Test
    void should_not_delete_the_same_content_again_when_run_twice() {
        job.run();
        job.run();

        verify(fileStorageProvider, times(1)).deleteByIdentifier(ATTACHMENT_OF_MESSAGE_3);
    }

    @Test
    void should_keep_the_original_deleted_at_when_run_twice() {
        job.run();
        var first = messageDeletedAt(MESSAGE_1);

        job.run();

        assertThat(messageDeletedAt(MESSAGE_1)).isEqualTo(first);
    }

    @TestConfiguration
    static class FixedClockConfiguration {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.now().plus(Duration.ofDays(30)), ZoneOffset.UTC);
        }
    }
}
