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

import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.application.port.spi.ConnectorBusinessDomainRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("checkstyle:MissingJavadocType")
@DisplayName("ConnectorBusinessDomainRepository")
public class ConnectorBusinessDomainRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ConnectorBusinessDomainRepository repository;

    @Nested
    @DisplayName("save a business domain")
    class Save {
        @Test
        void should_save_the_business_domain() {
            var domain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

            var savedDomain = repository.save(domain);

            assertThat(savedDomain).isNotNull();
            assertThat(domain.uuid()).isNull();
            assertThat(savedDomain.uuid()).isNotNull();
            assertThat(savedDomain.identifier()).isEqualTo(domain.identifier());
            assertThat(savedDomain.description()).isEqualTo(domain.description());
            assertThat(savedDomain.source()).isEqualTo(domain.source());
            assertThat(savedDomain.enabled()).isEqualTo(domain.enabled());
            assertThat(savedDomain.createdAt()).isNotNull();
            assertThat(savedDomain.updatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("find by identifier")
    class FindByIdentifier {
        @Test
        void should_find_the_business_domain() {
            var domain = BusinessDomainTestFixtures.createDefaultBusinessDomain();
            repository.save(domain);

            var foundDomain = repository.findByIdentifier(domain.identifier());

            assertThat(foundDomain).isNotNull();
            assertThat(foundDomain.identifier()).isEqualTo(domain.identifier());
            assertThat(foundDomain.description()).isEqualTo(domain.description());
            assertThat(foundDomain.source()).isEqualTo(domain.source());
            assertThat(foundDomain.enabled()).isEqualTo(domain.enabled());
            assertThat(foundDomain.createdAt()).isNotNull();
            assertThat(foundDomain.updatedAt()).isNotNull();
        }

        @Test
        void should_return_null_when_the_identifier_is_unknown() {
            var domain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

            var foundDomain = repository.findByIdentifier(domain.identifier());

            assertThat(foundDomain).isNull();
        }
    }

    @Nested
    @DisplayName("find all")
    class FindAll {
        @Test
        void should_return_all_the_business_domains() {
            var domain = BusinessDomainTestFixtures.createDefaultBusinessDomain();
            repository.save(domain);

            var domains = repository.findAll();

            assertThat(domains).hasSize(1);
        }
    }
}
