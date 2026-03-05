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
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.JpaContextConfiguration;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("DataFlowIssue")
@SpringBootTest(classes = JpaContextConfiguration.class)
public class ConnectorMessageRepositoryTest {
    @Autowired
    private ConnectorMessageRepository repository;

    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    @Sql("classpath:sql/party.sql")
    @Sql("classpath:sql/service.sql")
    @Sql("classpath:sql/action.sql")
    void should_save_connector_message_successfully_to_database() {
        var message = MessageTestFixtures.createValidOutboundStagingBusinessMessage()
                .toBuilder()
                .identifier("2adede33-41cf-4509-b0c1-0c83ad96eed7@connector.ecodex.eu")
                .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                .build();

        var saved = this.repository.save(message);

        assertThat(saved).isNotNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_connector_message_with_a_null_message_to_database() {
        assertThrows(
                NullPointerException.class, () -> repository.save(null)
        );
    }
}
