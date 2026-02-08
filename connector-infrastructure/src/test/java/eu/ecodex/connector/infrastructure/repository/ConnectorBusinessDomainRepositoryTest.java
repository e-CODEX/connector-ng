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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.infrastructure.database.repository.ConnectorBusinessDomainJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@DataJpaTest
@SuppressWarnings("checkstyle:MissingJavadocType")
public class ConnectorBusinessDomainRepositoryTest {
    @MockitoSpyBean
    private ConnectorBusinessDomainJpaRepository jpaRepository;

    private final ConnectorBusinessDomainRepository repository;

    @Autowired
    public ConnectorBusinessDomainRepositoryTest(ConnectorBusinessDomainRepository repository) {
        this.repository = repository;
    }

    @Test
    void should_save_business_domain_successfully_to_database() {
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

        verify(jpaRepository).save(any());
    }

    @Test
    void should_find_business_domain_by_identifier_successfully_from_database() {
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
    void should_return_null_when_searching_unknown_business_domain_by_identifier_from_database() {
        var domain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

        var foundDomain = repository.findByIdentifier(domain.identifier());

        assertThat(foundDomain).isNull();
    }
}
