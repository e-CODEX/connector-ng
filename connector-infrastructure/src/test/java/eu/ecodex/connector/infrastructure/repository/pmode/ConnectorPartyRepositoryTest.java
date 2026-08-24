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
import eu.ecodex.connector.application.port.spi.pmode.ConnectorPartyRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;


@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:LineLength", "DataFlowIssue"})
@DisplayName("ConnectorPartyRepository")
public class ConnectorPartyRepositoryTest extends AbstractRepositoryTest {
    private static final ConnectorBusinessDomainIdentifier DEFAULT_BUSINESS_DOMAIN =
        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier();
    @Autowired
    private ConnectorPartyRepository repository;

    /**
     * Reference data (business domain + processing mode), without parties.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
    })
    private @interface WithProcessingModeData {
    }

    /**
     * Reference data plus seeded parties.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/party.sql",
    })
    private @interface WithPartyData {
    }

    @Nested
    @DisplayName("save all")
    class SaveAll {
        @Test
        @WithProcessingModeData
        void should_save_all_the_parties() {
            var party = PartyTestFixtures.createToParty();

            var savedParties = repository.saveAll(List.of(party), DEFAULT_BUSINESS_DOMAIN);

            assertThat(savedParties).isNotNull();
            assertThat(savedParties).hasSize(1);
        }

        @Test
        void should_fail_when_the_parties_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.saveAll(null, DEFAULT_BUSINESS_DOMAIN)
            );
        }

        @Test
        void should_fail_when_the_business_domain_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.saveAll(List.of(PartyTestFixtures.createToParty()), null)
            );
        }

        @Test
        void should_fail_when_both_arguments_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.saveAll(null, null)
            );
        }
    }

    @Nested
    @DisplayName("find by identifier, role and business domain")
    class FindByIdentifierRoleAndBusinessDomain {
        @Test
        @WithPartyData
        void should_find_the_party() {
            var found = repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                "BL",
                ConnectorPartyRoleType.INITIATOR,
                DEFAULT_BUSINESS_DOMAIN
            );

            assertThat(found).isNotNull();
        }

        @Test
        @WithPartyData
        void should_return_null_when_the_party_identifier_is_unknown() {
            var found = repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                "AT",
                ConnectorPartyRoleType.INITIATOR,
                DEFAULT_BUSINESS_DOMAIN
            );

            assertThat(found).isNull();
        }

        @Test
        void should_fail_when_the_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                    null,
                    ConnectorPartyRoleType.INITIATOR,
                    DEFAULT_BUSINESS_DOMAIN
                )
            );
        }

        @Test
        void should_fail_when_the_role_type_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                    "AT",
                    null,
                    DEFAULT_BUSINESS_DOMAIN
                )
            );
        }

        @Test
        void should_fail_when_the_business_domain_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByIdentifierAndRoleTypeAndBusinessDomain(
                    "AT",
                    ConnectorPartyRoleType.INITIATOR,
                    null
                )
            );
        }

        @Test
        void should_fail_when_all_arguments_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByIdentifierAndRoleTypeAndBusinessDomain(null, null, null)
            );
        }
    }

    @Nested
    @DisplayName("find all by business domain identifier")
    class FindAllByBusinessDomainIdentifier {
        @Test
        @WithPartyData
        void should_find_all_the_parties() {
            var parties = repository.findAllByBusinessDomainIdentifier(DEFAULT_BUSINESS_DOMAIN);

            assertThat(parties).isNotNull();
            assertThat(parties).hasSize(4);
        }
    }
}
