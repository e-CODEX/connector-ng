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

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.JpaContextConfiguration;
import eu.ecodex.connector.ServiceTestFixtures;
import eu.ecodex.connector.domain.spi.ConnectorServiceRepository;
import eu.ecodex.connector.infrastructure.outbound.persistence.repository.ConnectorServiceJpaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(classes = JpaContextConfiguration.class)
@SuppressWarnings({"checkstyle:MissingJavadocType", "DataFlowIssue", "checkstyle:LineLength"})
public class ConnectorServiceRepositoryTest {
    @Autowired
    private ConnectorServiceRepository repository;
    @MockitoSpyBean
    private ConnectorServiceJpaRepository jpaRepository;

    // bulk saving
    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    void should_bulk_save_services_to_database_successfully() {
        var service = ServiceTestFixtures.createService();

        var savedServices = repository.saveAll(
                List.of(service),
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(savedServices).isNotNull();
        assertThat(savedServices).hasSize(1);

        verify(jpaRepository, times(1)).saveAll(anyList());
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_services_with_null_list_of_services() {
        assertThrows(
                NullPointerException.class, () -> repository.saveAll(
                        null,
                        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_services_with_null_business_domain_identifier() {
        assertThrows(
                NullPointerException.class, () -> repository.saveAll(
                        List.of(ServiceTestFixtures.createService()),
                        null
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_services_with_null_list_of_services_and_business_domain_identifier() {
        assertThrows(
                NullPointerException.class, () -> repository.saveAll(
                        null,
                        null
                )
        );
    }
}
