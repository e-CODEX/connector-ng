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
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("DataFlowIssue")
@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id IS NOT NULL",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class ConnectorMessageAttachmentRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ConnectorMessageAttachmentRepository repository;

    // saving
    @Test
    void should_save_attachment_to_database_successfully() {
        var attachment = MessageAttachmentTestFixtures.createAttachment();

        var savedAttachment = this.repository.save(attachment);

        assertThat(savedAttachment).isNotNull();
        assertThat(savedAttachment.identifier()).contains("_test_attachment");
        assertThat(savedAttachment.name()).isEqualTo("test_attachment.txt");
        assertThat(savedAttachment.size()).isEqualTo(100L);
        assertThat(savedAttachment.contentType()).isEqualTo("text/plain");
        assertThat(savedAttachment.description()).isNotBlank();
        assertThat(savedAttachment.storage()).isEqualTo(ConnectorAttachmentStorage.S3_BUCKET);
    }

    // finding by identifier
    @Test
    void should_find_attachment_by_identifier_successfully_from_database() {
        var attachment = MessageAttachmentTestFixtures.createAttachment();

        this.repository.save(attachment);
        var foundAttachment = this.repository.findByIdentifier(attachment.identifier());

        assertThat(foundAttachment).isNotNull();
        assertThat(foundAttachment.identifier()).isEqualTo(attachment.identifier());
    }

    @Test
    void should_return_null_when_searching_unknown_attachment_by_identifier_from_database() {
        var attachment = MessageAttachmentTestFixtures.createAttachment();

        var foundAttachment = this.repository.findByIdentifier(attachment.identifier());

        assertThat(foundAttachment).isNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_attachment_by_null_identifier_from_database() {
        assertThrows(
                NullPointerException.class, () -> this.repository.findByIdentifier(null)
        );
    }

    // finding all

    @Test
    void should_find_all_attachments_successfully_from_database() {
        var attachment = MessageAttachmentTestFixtures.createAttachment();
        this.repository.save(attachment);

        var pageRequest = ConnectorPageRequest.builder().page(0).size(20).build();
        var attachments = this.repository.findAll(pageRequest);

        assertThat(attachments).isNotNull();
        assertThat(attachments.content()).hasSize(1);
        assertThat(attachments.size()).isEqualTo(1);
        assertThat(attachments.totalElements()).isEqualTo(1L);
        assertThat(attachments.totalPages()).isEqualTo(1);
    }

    // attach to message

    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    @Sql("classpath:sql/message.sql")
    @Sql("classpath:sql/attachment.sql")
    void should_attach_a_message_to_an_attachment_successfully_in_database() {
        repository.attachToMessage(
                "d98a621a-4d14-4cfb-be00-0feae9f9b277_fake_file",
                "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu"
        );
    }

    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    @Sql("classpath:sql/message.sql")
    @Sql("classpath:sql/attachment.sql")
    void should_fail_to_attach_a_message_to_an_attachment_if_it_has_already_been_attached_in_database() {
        assertThrows(
                RuntimeException.class,
                () -> repository.attachToMessage(
                        "6aeef356-d580-4b94-a569-250435ac3ec5_fake_file",
                        "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu"
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_attaching_a_message_to_an_attachment_with_a_null_message_identifier_in_database() {
        assertThrows(
                NullPointerException.class,
                () -> repository.attachToMessage(
                        "6aeef356-d580-4b94-a569-250435ac3ec5_fake_file",
                        null
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_attaching_a_message_to_an_attachment_with_a_null_attachment_identifier_in_database() {
        assertThrows(
                NullPointerException.class,
                () -> repository.attachToMessage(
                        null,
                        "e4d9a3a5-42e8-4eeb-9236-678ecfbc0eb4"
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_attaching_a_message_to_an_attachment_with_a_null_message_and_attachment_identifiers_in_database() {
        assertThrows(
                NullPointerException.class,
                () -> repository.attachToMessage(
                        null,
                        null
                )
        );
    }
}
