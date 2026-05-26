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

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.PartyTestFixtures;
import eu.ecodex.connector.RepositoryContextConfiguration;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.spi.pmode.ConnectorPartyRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@SpringBootTest(classes = RepositoryContextConfiguration.class)
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

    // find by identifier, role and business domain identifier

    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    @Sql("classpath:sql/party.sql")
    void should_find_party_by_identifier_role_and_business_domain_identifier_successfully_from_database() {
        var found = this.repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                "BL",
                ConnectorPartyRoleType.INITIATOR,
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(found).isNotNull();
    }

    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    @Sql("classpath:sql/party.sql")
    void should_return_null_when_searching_party_by_unknown_party_identifier_from_database() {
        var found = this.repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                "AT",
                ConnectorPartyRoleType.INITIATOR,
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(found).isNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_party_by_null_identifier_from_database() {
        assertThrows(
                NullPointerException.class, () -> this.repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                        null,
                        ConnectorPartyRoleType.INITIATOR,
                        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_party_by_null_role_type_from_database() {
        assertThrows(
                NullPointerException.class, () -> this.repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                        "AT",
                        null,
                        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_party_by_null_business_domain_identifier_from_database() {
        assertThrows(
                NullPointerException.class, () -> this.repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                        "AT",
                        ConnectorPartyRoleType.INITIATOR,
                        null
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_party_by_null_identifier_role_and_business_domain_identifier_from_database() {
        assertThrows(
                NullPointerException.class, () -> this.repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                        null,
                        null,
                        null
                )
        );
    }
}
