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
import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.spi.pmode.ConnectorProcessingModeRepository;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:LineLength", "DataFlowIssue"})
public class ConnectorProcessingModeRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ConnectorProcessingModeRepository repository;

    @Test
    @Sql("classpath:sql/business-domain.sql")
    void should_save_pmode_successfully_to_database() {
        var processingMode = ProcessingModeTestFixtures.createWithBusinessDomain();

        var savedProcessingMode = repository.save(
                processingMode, processingMode.businessDomain().identifier()
        );

        assertThat(savedProcessingMode).isNotNull();
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

    // find all
    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    void should_find_all_pmodes_successfully_from_database() {
        var processingModes = repository.findAll();

        assertThat(processingModes).isNotNull();
        assertThat(processingModes).hasSize(1);
    }
}
