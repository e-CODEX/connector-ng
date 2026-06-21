/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorMessageDocumentTestFixtures;
import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.ConnectorSecurityToolkit;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.infrastructure.security.exception.ConnectorContainerException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("DataFlowIssue")
public class ConnectorSecurityToolkitTest extends BaseContainerTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ConnectorSecurityToolkit securityToolkit;
    @MockitoBean
    private ConnectorFileStorageProvider fileStorageProvider;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM connector_message_attachments");
        jdbcTemplate.execute("DELETE FROM connector_messages");
        jdbcTemplate.execute("DELETE FROM connector_processing_modes");
        jdbcTemplate.execute("DELETE FROM connector_business_domains");
    }

    @Test
    void should_build_asics_container_sign_it_and_push_for_process_successfully() {
        when(fileStorageProvider.save(any(), (Path) any())).thenReturn(UUID.randomUUID().toString());
        when(fileStorageProvider.findByIdentifier(any()))
                .thenReturn(FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf"));

        var message = createMessage();

        var outputMessage = securityToolkit.buildContainer(message);

        assertThat(outputMessage).isNotNull();
    }

    @Test
    void should_build_asics_container_sign_it_and_push_for_process_successfully_2() {
        // message with attachment
        when(fileStorageProvider.save(any(), (Path) any())).thenReturn(UUID.randomUUID().toString());
        when(fileStorageProvider.findByIdentifier(any()))
                .thenReturn(FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf"))
                .thenReturn(FileTestFixtures.readAsBytes("raw/attachment/Attachment.png"));

        var message = createMessageWithAttachment();

        var outputMessage = securityToolkit.buildContainer(message);

        assertThat(outputMessage).isNotNull();
    }

    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    @Sql("classpath:sql/message.sql")
    @Sql("classpath:sql/attachment.sql")
    @Disabled("This test is not working, but it should")
    void should_throw_exception_when_building_asics_container_if_the_message_has_two_identical_attachments() {
        // message with attachment
        var message = createMessageWithIdenticalAttachmentNames();

        assertThrows(
                ConnectorContainerException.class,
                () -> securityToolkit.buildContainer(message)
        );
    }

    @Test
    void should_throw_exception_when_building_asics_container_if_the_s3_provider_is_not_available() {
        // message with attachment
        doThrow(RuntimeException.class).when(fileStorageProvider).save(any(), (Path) any());
        when(fileStorageProvider.findByIdentifier(any()))
                .thenReturn(FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf"));

        var message = createMessage();

        assertThrows(
                ConnectorContainerException.class,
                () -> securityToolkit.buildContainer(message)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_building_asics_container_if_the_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> securityToolkit.buildContainer(null)
        );
    }

    @Test
    void should_throw_exception_when_building_asics_container_if_the_message_identifier_is_null() {
        var message = MessageTestFixtures.createOutboundBusinessMessage()
                                         .toBuilder()
                                         .identifier(null)
                                         .build();
        assertThrows(
                ConnectorContainerException.class,
                () -> securityToolkit.buildContainer(message)
        );
    }

    private ConnectorMessage createMessage() {
        return MessageTestFixtures
                .createOutboundBusinessMessage()
                .toBuilder()
                .businessContent(
                        MessageContentTestFixtures
                                .createContent()
                                .toBuilder()
                                .xmlContent(MessageAttachmentTestFixtures.createBusinessContentAttachment())
                                .businessDocument(
                                        ConnectorMessageDocumentTestFixtures
                                                .createDocumentWithoutSignature()
                                )
                                .build()
                )
                .build();
    }

    private ConnectorMessage createMessageWithAttachment() {
        return createMessage()
                .toBuilder()
                .identifier("fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu")
                .attachments(
                        List.of(
                                MessageAttachmentTestFixtures.createAttachment()
                        )
                )
                .build();
    }

    private ConnectorMessage createMessageWithIdenticalAttachmentNames() {
        return createMessage()
                .toBuilder()
                .identifier("fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu")
                .attachments(
                        List.of(
                                MessageAttachmentTestFixtures.createAttachment(),
                                MessageAttachmentTestFixtures.createAttachment(),
                                MessageAttachmentTestFixtures.createAttachment()
                        )
                )
                .build();
    }
}
