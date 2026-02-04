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

import eu.ecodex.connector.domain.api.service.ConnectorMessageContentService;
import eu.ecodex.connector.domain.spi.ConnectorMessageContentRepository;
import eu.ecodex.connector.utils.MessageContentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorMessageContentService} implementation.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorMessageContentServiceTest {
    @Mock
    private ConnectorMessageContentRepository messageContentRepository;
    private ConnectorMessageContentService connectorMessageContentService;

    @BeforeEach
    void setUp() {
        connectorMessageContentService = new ConnectorMessageContentServiceImpl(
                messageContentRepository
        );
    }

    @Test
    void should_register_message_content_successfully() {
        var messageContent = MessageContentUtil.createContent();

        when(messageContentRepository.save(any())).thenReturn(MessageContentUtil.createSaveContent());

        var savedContent = connectorMessageContentService.register(messageContent);

        assertThat(savedContent).isNotNull();
        assertThat(savedContent.xmlContent()).isEqualTo(messageContent.xmlContent());
        assertThat(savedContent.document()).isNotNull();
    }

    @Test
    void should_throw_null_pointer_exception_if_message_content_to_register_is_null() {
        assertThrows(
                NullPointerException.class, () -> connectorMessageContentService.register(null)
        );
    }
}
