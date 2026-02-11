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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import eu.ecodex.connector.infrastructure.database.repository.ConnectorProcessingModeJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:LineLength", "DataFlowIssue"})
public class ConnectorProcessingModeRepositoryTest {
    @Autowired
    private ConnectorProcessingModeRepository repository;
    @MockitoSpyBean
    private ConnectorProcessingModeJpaRepository jpaRepository;

    @Test
    @Sql("classpath:sql/business-domain.sql")
    void should_save_pmode_successfully_to_database() {
        var processingMode = ProcessingModeTestFixtures.createWithBusinessDomain();

        var savedProcessingMode = repository.save(
                processingMode, processingMode.businessDomain().identifier()
        );

        assertThat(savedProcessingMode).isNotNull();

        verify(jpaRepository, times(1)).save(any());
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_pmode_with_a_null_pmode_and_business_domain_identifier() {
        assertThrows(
                NullPointerException.class, () -> repository.save(null, null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_pmode_with_a_null_pmode() {
        assertThrows(
                NullPointerException.class, () -> repository.save(
                        null,
                        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_pmode_with_a_null_business_domain_identifier() {
        assertThrows(
                NullPointerException.class, () -> repository.save(
                        ProcessingModeTestFixtures.createWithBusinessDomain(), null
                )
        );
    }

    // update
    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    @Sql("classpath:sql/keystore.sql")
    void should_update_Keystore_an_existing_pmode_successfully_from_database() {
        var updatedProcessingMode = repository.updateKeystore(
                "4f10aed9-2e5f-4780-87f7-5fe1070d5ccf",
                "f81647fc-d870-4275-bdbd-982f32e5235f"
        );

        assertThat(updatedProcessingMode).isNotNull();
        // assertThat(updatedProcessingMode.truststore()).isNotNull();

        verify(jpaRepository).save(any());
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_pmode_with_a_null_pmode() {
        assertThrows(
                NullPointerException.class, () -> repository.updateKeystore(null, null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_pmode_with_a_null_uuid() {
        assertThrows(
                NullPointerException.class,
                () -> repository.updateKeystore(null, "f81647fc-d870-4275-bdbd-982f32e5235f")
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_pmode_with_a_null_keystore_uuid() {
        assertThrows(
                NullPointerException.class,
                () -> repository.updateKeystore("4f10aed9-2e5f-4780-87f7-5fe1070d5ccf", null)
        );
    }

    // find by uuid
    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    void should_find_pmode_by_uuid_successfully_from_database() {
        var processingMode = repository.findByUuid(
                "4f10aed9-2e5f-4780-87f7-5fe1070d5ccf"
        );

        assertThat(processingMode).isNotNull();
        assertThat(processingMode.uuid()).isNotNull();
    }

    @Test
    void should_return_null_when_searching_pmode_by_unknown_uuid_from_database() {
        var processingMode = repository.findByUuid("unknown-uuid");
        assertThat(processingMode).isNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_pmode_with_a_null_uuid() {
        assertThrows(
                NullPointerException.class, () -> repository.findByUuid(null)
        );
    }

    // find by business domain identifier
    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    void should_find_pmode_by_business_domain_identifier_successfully_from_database() {
        var processingMode = repository.findByBusinessDomainIdentifier(
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(processingMode).isNotNull();
        assertThat(processingMode.businessDomain().identifier())
                .isEqualTo(
                        BusinessDomainIdentifierTestFixtures
                                .createDefaultBusinessDomainIdentifier()
                );
    }

    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    void should_return_null_when_searching_pmode_by_unknown_business_domain_identifier_from_database() {
        var processingMode = repository.findByBusinessDomainIdentifier(
                ConnectorBusinessDomainIdentifier.builder().build()
        );

        assertThat(processingMode).isNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_pmode_with_a_null_business_domain_identifier() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.findByBusinessDomainIdentifier(null)
        );
    }
}
