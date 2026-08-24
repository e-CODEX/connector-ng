/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository.message;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("DataFlowIssue")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id IS NOT NULL",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DisplayName("ConnectorMessageAttachmentRepository")
public class ConnectorMessageAttachmentRepositoryTest extends AbstractRepositoryTest {
    private static final String MESSAGE_ID =
        "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu";
    @Autowired
    private ConnectorMessageAttachmentRepository repository;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/message.sql",
        "classpath:sql/attachment.sql",
    })
    private @interface WithMessageAndAttachmentData {
    }

    @Nested
    @DisplayName("save an attachment")
    class Save {
        @Test
        void should_save_the_attachment() {
            var attachment = MessageAttachmentTestFixtures.createAttachment();

            var savedAttachment = repository.save(attachment);

            assertThat(savedAttachment).isNotNull();
            assertThat(savedAttachment.identifier()).contains("_test_attachment");
            assertThat(savedAttachment.name()).isEqualTo("test_attachment.txt");
            assertThat(savedAttachment.size()).isEqualTo(100L);
            assertThat(savedAttachment.contentType()).isEqualTo("text/plain");
            assertThat(savedAttachment.description()).isNotBlank();
            assertThat(savedAttachment.storage()).isEqualTo(ConnectorAttachmentStorage.S3_BUCKET);
        }
    }

    @Nested
    @DisplayName("find by identifier")
    class FindByIdentifier {
        @Test
        void should_find_the_attachment() {
            var attachment = MessageAttachmentTestFixtures.createAttachment();
            repository.save(attachment);

            var foundAttachment = repository.findByIdentifier(attachment.identifier());

            assertThat(foundAttachment).isNotNull();
            assertThat(foundAttachment.identifier()).isEqualTo(attachment.identifier());
        }

        @Test
        void should_return_null_when_the_identifier_is_unknown() {
            var attachment = MessageAttachmentTestFixtures.createAttachment();

            var foundAttachment = repository.findByIdentifier(attachment.identifier());

            assertThat(foundAttachment).isNull();
        }

        @Test
        void should_throw_when_the_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByIdentifier(null)
            );
        }
    }

    @Nested
    @DisplayName("find all")
    class FindAll {
        @Test
        void should_return_all_the_attachments() {
            var attachment = MessageAttachmentTestFixtures.createAttachment();
            repository.save(attachment);

            var pageRequest = ConnectorPageRequest.builder().page(0).size(20).build();
            var attachments = repository.findAll(pageRequest);

            assertThat(attachments).isNotNull();
            assertThat(attachments.content()).hasSize(1);
            assertThat(attachments.size()).isEqualTo(1);
            assertThat(attachments.totalElements()).isEqualTo(1L);
            assertThat(attachments.totalPages()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("attach to message")
    class AttachToMessage {
        @Test
        @WithMessageAndAttachmentData
        void should_attach_the_message_to_the_attachment() {
            repository.attachToMessage(
                "d98a621a-4d14-4cfb-be00-0feae9f9b277_fake_file",
                MESSAGE_ID
            );
        }

        @Test
        @WithMessageAndAttachmentData
        void should_fail_when_the_attachment_is_already_attached() {
            assertThrows(
                RuntimeException.class,
                () -> repository.attachToMessage(
                    "6aeef356-d580-4b94-a569-250435ac3ec5_fake_file",
                    MESSAGE_ID
                )
            );
        }

        @Test
        void should_throw_when_the_message_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.attachToMessage(
                    "6aeef356-d580-4b94-a569-250435ac3ec5_fake_file",
                    null
                )
            );
        }

        @Test
        void should_throw_when_the_attachment_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.attachToMessage(
                    null,
                    "e4d9a3a5-42e8-4eeb-9236-678ecfbc0eb4"
                )
            );
        }

        @Test
        void should_throw_when_both_identifiers_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.attachToMessage(null, null)
            );
        }
    }
}
