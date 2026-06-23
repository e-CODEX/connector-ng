/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.ConnectorRetrieveMessageService;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorRetrieveMessageServiceTest {
    @Mock
    private ConnectorMessageRepository messageRepository;

    @InjectMocks
    private ConnectorRetrieveMessageService retrieveMessageService;

    @Test
    void should_throw_null_pointer_exception_if_message_identifier_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> retrieveMessageService.execute(null)
        );
    }

    @Test
    void should_throw_exception_if_message_is_not_found() {
        when(messageRepository.findByIdentifier(any())).thenReturn(null);

        assertThrows(
                ConnectorMessageNotFoundException.class,
                () -> retrieveMessageService.execute("message-identifier")
        );
    }

    @Test
    void should_retrieve_message_by_identifier_successfully() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        when(messageRepository.findByIdentifier(any())).thenReturn(message);

        var retrievedMessage = retrieveMessageService.execute(
                "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu");

        assertThat(retrievedMessage).isNotNull();
        assertThat(retrievedMessage).isEqualTo(message);
    }
}
