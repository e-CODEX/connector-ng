/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.container;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.infrastructure.security.BaseContainerTest;
import eu.ecodex.connector.infrastructure.security.exception.ConnectorContainerException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

@Transactional
@SuppressWarnings("DataFlowIssue")
public class ConnectorAsicContainerValidatorTest extends BaseContainerTest {
    @Autowired
    private ConnectorAsicContainerValidator asicContainerValidator;
    @Autowired
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @MockitoBean
    private ConnectorFileStorageProvider fileStorageProvider;

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql",
            "classpath:sql/party.sql",
            "classpath:sql/service.sql",
            "classpath:sql/action.sql",
            "classpath:sql/message.sql",
            "classpath:sql/message-as4-properties.sql",
            "classpath:sql/attachment.sql"
    })
    void should_validate_asics_container_with_attachments_successfully() {
        when(fileStorageProvider.findByIdentifier(any()))
                .thenReturn(FileTestFixtures.readAsBytes("raw/asic/asics_with_att.asics"))
                .thenReturn(FileTestFixtures.readAsBytes("raw/token/trust-ok.xml"));

        var message = generateMessage();

        asicContainerValidator.validate(message);

        var attachments = attachmentRepository.findByMessageIdentifierAndTypes(
                message.identifier(),
                List.of(
                        ConnectorAttachmentType.PDF_TOKEN,
                        ConnectorAttachmentType.BUSINESS_DOCUMENT,
                        ConnectorAttachmentType.ATTACHMENT
                )
        );

        assertThat(attachments).isNotNull();
        assertThat(attachments.size()).isEqualTo(3);
    }

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql",
            "classpath:sql/party.sql",
            "classpath:sql/service.sql",
            "classpath:sql/action.sql",
            "classpath:sql/message.sql",
            "classpath:sql/message-as4-properties.sql",
            "classpath:sql/attachment.sql"
    })
    void should_validate_asics_container_with_no_attachments_successfully() {
        when(fileStorageProvider.findByIdentifier(any()))
                .thenReturn(FileTestFixtures.readAsBytes("raw/asic/no_att_asic.asics"))
                .thenReturn(FileTestFixtures.readAsBytes("raw/token/trust-ok-2.xml"));

        var message = generateMessage();

        asicContainerValidator.validate(message);

        var attachments = attachmentRepository.findByMessageIdentifierAndTypes(
                message.identifier(),
                List.of(
                        ConnectorAttachmentType.PDF_TOKEN,
                        ConnectorAttachmentType.BUSINESS_DOCUMENT,
                        ConnectorAttachmentType.ATTACHMENT
                )
        );

        assertThat(attachments).isNotNull();
        assertThat(attachments.size()).isEqualTo(2);
    }

    @Test
    void should_thrown_exception_when_validating_asics_container_if_the_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> asicContainerValidator.validate(null)
        );
    }

    @Test
    void should_thrown_exception_when_validating_asics_container_if_the_message_attachments_is_null() {
        var message = generateMessage().toBuilder().attachments(null).build();

        assertThrows(
                IllegalArgumentException.class,
                () -> asicContainerValidator.validate(message)
        );
    }

    @Test
    void should_thrown_exception_when_validating_asics_container_if_the_message_has_no_attachments() {
        var message = generateMessage().toBuilder().attachments(List.of()).build();

        assertThrows(
                IllegalArgumentException.class,
                () -> asicContainerValidator.validate(message)
        );
    }

    @Test
    void should_thrown_exception_when_validating_asics_container_if_the_asics_attachment_is_inexistent() {
        var message = generateMessage().toBuilder().attachments(List.of(
                MessageAttachmentTestFixtures.createXmlTokenAttachment())).build();

        assertThrows(
                IllegalArgumentException.class,
                () -> asicContainerValidator.validate(message)
        );
    }

    @Test
    void should_thrown_exception_when_validating_asics_container_if_the_xml_token_attachment_is_inexistent() {
        var message = generateMessage().toBuilder().attachments(List.of(
                MessageAttachmentTestFixtures.createAsicsAttachment())).build();

        assertThrows(
                IllegalArgumentException.class,
                () -> asicContainerValidator.validate(message)
        );
    }

    @Test
    void should_thrown_exception_when_validating_asics_container_if_asics_content_is_empty() {
        when(fileStorageProvider.findByIdentifier(any()))
                .thenReturn(new byte[0])
                .thenReturn(FileTestFixtures.readAsBytes("raw/token/trust-ok.xml"));

        var message = generateMessage();

        assertThrows(
                ConnectorContainerException.class,
                () -> asicContainerValidator.validate(message)
        );
    }

    @Test
    void should_thrown_exception_when_validating_asics_container_if_xml_token_content_is_empty() {
        when(fileStorageProvider.findByIdentifier(any()))
                .thenReturn(FileTestFixtures.readAsBytes("raw/asic/asics_with_att.asics"))
                .thenReturn(new byte[0]);

        var message = generateMessage();

        assertThrows(
                ConnectorContainerException.class,
                () -> asicContainerValidator.validate(message)
        );
    }

    @Test
    void should_thrown_exception_when_validating_asics_container_if_asics_file_is_not_a_zip_file() {
        when(fileStorageProvider.findByIdentifier(any()))
                .thenReturn(new byte[1])
                .thenReturn(FileTestFixtures.readAsBytes("raw/token/trust-ok.xml"));

        var message = generateMessage();

        assertThrows(
                ConnectorContainerException.class,
                () -> asicContainerValidator.validate(message)
        );
    }

    @Test
    void should_thrown_exception_when_validating_asics_container_if_xml_token_file_is_not_an_xml_file() {
        when(fileStorageProvider.findByIdentifier(any()))
                .thenReturn(FileTestFixtures.readAsBytes("raw/asic/asics_with_att.asics"))
                .thenReturn(new byte[1]);

        var message = generateMessage();

        assertThrows(
                ConnectorContainerException.class,
                () -> asicContainerValidator.validate(message)
        );
    }

    private ConnectorMessage generateMessage() {
        return MessageTestFixtures.createValidInboundBusinessMessage()
                                  .toBuilder()
                                  .identifier(
                                          "7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu")
                                  .businessContent(MessageContentTestFixtures.createContentWithoutBusinessDocument())
                                  .attachments(
                                          List.of(
                                                  MessageAttachmentTestFixtures.createAsicsAttachment(),
                                                  MessageAttachmentTestFixtures.createXmlTokenAttachment()
                                          )
                                  )
                                  .evidences(List.of(
                                          EvidenceTestFixtures.createSubmissionAcceptanceEvidence()
                                  ))
                                  .build();
    }
}
