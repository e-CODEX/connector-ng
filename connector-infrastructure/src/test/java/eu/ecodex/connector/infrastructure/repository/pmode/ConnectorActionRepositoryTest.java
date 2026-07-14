/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository.pmode;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.ActionTestFixtures;
import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.domain.spi.pmode.ConnectorActionRepository;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings({"checkstyle:MissingJavadocType", "DataFlowIssue", "checkstyle:LineLength"})
public class ConnectorActionRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ConnectorActionRepository repository;

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

    // find by name and business domain identifier

    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    @Sql("classpath:sql/action.sql")
    void should_find_action_by_name_and_business_domain_identifier_successfully_from_database() {
        var action = repository.findByNameAndBusinessDomain(
                "Test_Form",
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(action).isNotNull();
        assertThat(action.name()).isEqualTo("Test_Form");
    }

    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    @Sql("classpath:sql/action.sql")
    void should_return_null_when_searching_action_by_unknown_name_and_business_domain_identifier_from_database() {
        var action = repository.findByNameAndBusinessDomain(
                "Test_Form_Unknown",
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(action).isNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_action_with_a_null_name_from_database() {
        assertThrows(
                NullPointerException.class, () -> repository.findByNameAndBusinessDomain(
                        null,
                        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_action_with_a_null_business_domain_identifier_from_database() {
        assertThrows(
                NullPointerException.class, () -> repository.findByNameAndBusinessDomain(
                        "Test_Form",
                        null
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_action_with_a_null_name_and_business_domain_identifier_from_database() {
        assertThrows(
                NullPointerException.class, () -> repository.findByNameAndBusinessDomain(
                        null,
                        null
                )
        );
    }
}
