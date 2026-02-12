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

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.JpaContextConfiguration;
import eu.ecodex.connector.PartyTestFixtures;
import eu.ecodex.connector.domain.spi.ConnectorPartyRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@SpringBootTest(classes = JpaContextConfiguration.class)
@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:LineLength", "DataFlowIssue"})
public class ConnectorPartyRepositoryTest {
    @Autowired
    private ConnectorPartyRepository repository;

    // bulk saving
    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    void should_bulk_save_parties_to_database_successfully() {
        var party = PartyTestFixtures.createToParty();

        var savedParties = repository.saveAll(
                List.of(party),
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(savedParties).isNotNull();
        assertThat(savedParties).hasSize(1);
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_parties_with_null_list_of_parties() {
        assertThrows(
                NullPointerException.class, () -> repository.saveAll(
                        null,
                        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_parties_with_null_business_domain_identifier() {
        assertThrows(
                NullPointerException.class, () -> repository.saveAll(
                        List.of(PartyTestFixtures.createToParty()),
                        null
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_parties_with_null_list_of_parties_and_business_domain_identifier() {
        assertThrows(
                NullPointerException.class, () -> repository.saveAll(
                        null,
                        null
                )
        );
    }
}
