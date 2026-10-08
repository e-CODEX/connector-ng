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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorListMessagesService")
public class ConnectorListMessagesTest {
    @Mock
    ConnectorMessageRepository messageRepository;

    @InjectMocks
    ConnectorListMessagesService connectorListMessages;

    @Test
    void should_return_paginated_messages_list() {
        var pageResult = ConnectorPageResult.of(
            List.of(BusinessMessageTestFixtures.createOutboundMessage()), 1, 1, 1
        );
        when(messageRepository.findAll(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(pageResult);

        var pageRequest = ConnectorPageRequest.builder().page(0).size(20).build();
        var result = connectorListMessages.execute(
            pageRequest, null, null, null, null, null, null
        );

        assertThat(result).isNotNull();
        assertThat(result.content()).isNotEmpty();
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(1);
    }
}
