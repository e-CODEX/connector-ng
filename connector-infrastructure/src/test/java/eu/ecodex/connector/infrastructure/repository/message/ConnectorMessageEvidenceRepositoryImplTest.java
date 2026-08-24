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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("DataFlowIssue")

@DisplayName("ConnectorMessageEvidenceRepository")
public class ConnectorMessageEvidenceRepositoryImplTest extends AbstractRepositoryTest {
    private static final String MESSAGE_ID =
        "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu";

    @Autowired
    private ConnectorMessageEvidenceRepository repository;

    @Test
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/message.sql",
    })
    void should_save_the_evidence() {
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();

        var saved = repository.save(evidence, MESSAGE_ID);

        assertThat(saved).isNotNull();
        assertThat(saved.uuid()).isNotNull();
        assertThat(saved.createdAt()).isNotNull();
        assertThat(saved.updatedAt()).isNotNull();
    }

    @Test
    void should_throw_when_the_message_identifier_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> repository.save(
                EvidenceTestFixtures.createSubmissionAcceptanceEvidence(),
                null
            )
        );
    }

    @Test
    void should_throw_when_the_evidence_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> repository.save(null, MESSAGE_ID)
        );
    }
}
