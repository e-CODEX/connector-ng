/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.domain.api.service.ConnectorMessageAttachmentService;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorMessageAttachmentService} implementation.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorMessageAttachmentServiceTest {
    @Mock
    private ConnectorMessageAttachmentRepository messageAttachmentRepository;
    private ConnectorMessageAttachmentService connectorMessageAttachmentService;


    @BeforeEach
    void setUp() {
        connectorMessageAttachmentService = new ConnectorMessageAttachmentServiceImpl(
                messageAttachmentRepository
        );
    }

    @Test
    void should_register_message_attachment_successfully() {
        var attachment = MessageAttachmentTestFixtures.createAttachment();

        when(messageAttachmentRepository.save(any()))
                .thenReturn(MessageAttachmentTestFixtures.createSavedAttachment());

        var savedAttachment = connectorMessageAttachmentService.register(attachment);

        assertThat(savedAttachment).isNotNull();
        assertThat(savedAttachment.mimeType()).isEqualTo(attachment.mimeType());
        assertThat(attachment.uuid()).isNull();
        assertThat(savedAttachment.uuid()).isNotEmpty();
        assertThat(savedAttachment.description()).isEqualTo(attachment.description());
    }

    @Test
    void should_throw_null_pointer_exception_if_attachment_to_register_is_null() {
        assertThrows(
                NullPointerException.class, () -> connectorMessageAttachmentService.register(null)
        );
    }
}
