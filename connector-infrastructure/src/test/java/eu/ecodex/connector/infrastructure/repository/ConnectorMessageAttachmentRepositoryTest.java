/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.JpaContextConfiguration;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(classes = JpaContextConfiguration.class)
public class ConnectorMessageAttachmentRepositoryTest {
    @Autowired
    private ConnectorMessageAttachmentRepository repository;

    @Test
    void should_save_attachment_to_database_successfully() {
        var attachment = MessageAttachmentTestFixtures.createAttachment();

        var savedAttachment = this.repository.save(attachment);

        assertThat(savedAttachment).isNotNull();
        assertThat(savedAttachment.identifier()).contains("_test_attachment");
        assertThat(savedAttachment.name()).isEqualTo("test_attachment");
        assertThat(savedAttachment.size()).isEqualTo(100L);
        assertThat(savedAttachment.contentType()).isEqualTo("text/plain");
        assertThat(savedAttachment.description()).isNotBlank();
        assertThat(savedAttachment.storage()).isEqualTo(ConnectorAttachmentStorage.S3_BUCKET);
    }

    @Test
    void should_find_all_attachments_successfully_from_database() {
        var attachment = MessageAttachmentTestFixtures.createAttachment();
        this.repository.save(attachment);

        var pageRequest = ConnectorPageRequest.builder().page(0).size(20).build();
        var attachments = this.repository.findAll(pageRequest);

        assertThat(attachments).isNotNull();
        assertThat(attachments.content()).hasSize(1);
        assertThat(attachments.totalElements()).isEqualTo(1L);
        assertThat(attachments.page()).isEqualTo(0);
        assertThat(attachments.size()).isEqualTo(20);
    }
}
