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

import eu.ecodex.connector.ConnectorMessageDocumentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.RepositoryContextConfiguration;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.spi.ConnectorMessageBusinessContentRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("DataFlowIssue")
@Transactional
@SpringBootTest(classes = RepositoryContextConfiguration.class)
public class ConnectorMessageBusinessContentRepositoryTest {
    @Autowired
    private ConnectorMessageBusinessContentRepository repository;

    private static Stream<ConnectorMessageBusinessContent> createContents() {
        return Stream.of(
                MessageContentTestFixtures.createContent(),
                MessageContentTestFixtures.createContent()
                                          .toBuilder()
                                          .businessDocument(
                                                  ConnectorMessageDocumentTestFixtures.createDocumentWithoutSignature())
                                          .build()
        );
    }

    @ParameterizedTest
    @MethodSource("createContents")
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    @Sql("classpath:sql/message.sql")
    @Sql("classpath:sql/attachment.sql")
    void should_save_message_business_content_successfully_to_database(ConnectorMessageBusinessContent businessContent) {
        var saved = this.repository.save(
                businessContent,
                "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu"
        );

        assertThat(saved).isNotNull();
    }

    @Test
    void should_thrown_null_pointer_exception_when_saving_business_content_with_null_content_to_database() {
        assertThrows(
                NullPointerException.class, () -> this.repository.save(
                        null, "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu"
                )
        );
    }

    @Test
    void should_thrown_null_pointer_exception_when_saving_business_content_with_null_message_identifier_to_database() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.save(MessageContentTestFixtures.createContent(), null)
        );
    }

    @Test
    void should_thrown_null_pointer_exception_when_saving_business_content_with_null_content_and_message_identifier_to_database() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.save(null, null)
        );
    }
}
