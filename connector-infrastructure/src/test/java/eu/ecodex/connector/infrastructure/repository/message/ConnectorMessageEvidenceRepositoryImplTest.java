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
import eu.ecodex.connector.RepositoryContextConfiguration;
import eu.ecodex.connector.domain.spi.ConnectorMessageEvidenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("DataFlowIssue")
@Transactional
@SpringBootTest(classes = RepositoryContextConfiguration.class)
public class ConnectorMessageEvidenceRepositoryImplTest {
    @Autowired
    private ConnectorMessageEvidenceRepository repository;

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql",
            "classpath:sql/message.sql",
            "classpath:sql/attachment.sql"
    })
    void should_save_evidence_successfully_to_database() {
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();

        var saved = repository.save(
                evidence,
                "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu"
        );

        assertThat(saved).isNotNull();
        assertThat(saved.uuid()).isNotNull();
        assertThat(saved.createdAt()).isNotNull();
        assertThat(saved.updatedAt()).isNotNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_evidence_with_null_message_identifier() {
        assertThrows(
                NullPointerException.class,
                () -> repository.save(
                        EvidenceTestFixtures.createSubmissionAcceptanceEvidence(),
                        null
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_evidence_with_null_evidence() {
        assertThrows(
                NullPointerException.class,
                () -> repository.save(
                        null,
                        "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu"
                )
        );
    }
}
