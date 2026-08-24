/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.ConnectorMessageDocumentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageBusinessContentRepository;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;


@SuppressWarnings("DataFlowIssue")
@DisplayName("ConnectorMessageBusinessContentRepository")
public class ConnectorMessageBusinessContentRepositoryTest extends AbstractRepositoryTest {
    private static final String MESSAGE_ID =
        "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu";

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
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/message.sql",
        "classpath:sql/attachment2.sql",
    })
    void should_save_the_business_content(ConnectorMessageBusinessContent businessContent) {
        var savedBusinessContent = repository.save(businessContent, MESSAGE_ID);

        assertNotNull(savedBusinessContent);
        assertNotNull(savedBusinessContent.xmlContent());

        if (savedBusinessContent.businessDocument() != null) {
            assertNotNull(savedBusinessContent.businessDocument().uuid());
            assertNotNull(savedBusinessContent.businessDocument().attachment());
            assertThat(savedBusinessContent.businessDocument().aesType()).isEqualTo(
                businessContent.businessDocument().aesType()
            );
            assertThat(savedBusinessContent.businessDocument().detachedSignature()).isEqualTo(
                businessContent.businessDocument().detachedSignature()
            );
        }
    }

    @Test
    void should_fail_when_the_content_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> repository.save(null, MESSAGE_ID)
        );
    }

    @Test
    void should_fail_when_the_message_identifier_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> repository.save(MessageContentTestFixtures.createContent(), null)
        );
    }

    @Test
    void should_throw_when_both_arguments_are_null() {
        assertThrows(
            NullPointerException.class,
            () -> repository.save(null, null)
        );
    }
}
