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
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.impl.message.ConnectorMessageEbmsIdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorMessageEbmsIdGeneratorTest {
    @Mock
    private ConnectorMessageProcessingConfigurationProvider messageProcessingConfigProvider;

    @InjectMocks
    private ConnectorMessageEbmsIdGenerator ebmsIdGenerator;

    @Test
    void should_generate_message_identifier_successfully() {
        when(messageProcessingConfigProvider.getConfiguration())
                .thenReturn(
                        ConnectorMessageProcessingConfiguration
                                .builder()
                                .ebmsIdSuffix("connector.ecodex.eu")
                                .build()
                );
        var ebmsIdentifier = ebmsIdGenerator.generateIdentifier();

        assertThat(ebmsIdentifier).isNotNull();
        assertThat(ebmsIdentifier).isNotBlank();
        assertThat(ebmsIdentifier).contains("connector.ecodex.eu");
    }
}
