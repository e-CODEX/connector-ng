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
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import eu.ecodex.connector.ActionTestFixtures;
import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.domain.spi.ConnectorActionRepository;
import eu.ecodex.connector.infrastructure.database.repository.ConnectorActionJpaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
@SuppressWarnings({"checkstyle:MissingJavadocType", "DataFlowIssue", "checkstyle:LineLength"})
public class ConnectorActionRepositoryTest {
    @Autowired
    private ConnectorActionRepository repository;
    @MockitoSpyBean
    private ConnectorActionJpaRepository jpaRepository;

    // bulk saving
    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    void should_bulk_save_actions_to_database() {
        var action = ActionTestFixtures.createAction();

        var savedAction = repository.saveAll(
                List.of(action),
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(savedAction).isNotNull();
        assertThat(savedAction).hasSize(1);

        verify(jpaRepository, times(1)).saveAll(anyList());
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_actions_with_null_list_of_actions() {
        assertThrows(
                NullPointerException.class, () -> repository.saveAll(
                        null,
                        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_actions_with_null_business_domain_identifier() {
        assertThrows(
                NullPointerException.class, () -> repository.saveAll(
                        List.of(ActionTestFixtures.createAction()),
                        null
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_actions_with_null_list_of_actions_and_business_domain_identifier() {
        assertThrows(
                NullPointerException.class, () -> repository.saveAll(
                        null,
                        null
                )
        );
    }
}
