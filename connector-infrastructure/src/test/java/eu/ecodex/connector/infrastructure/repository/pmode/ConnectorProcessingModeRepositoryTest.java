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
import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:LineLength", "DataFlowIssue"})
@DisplayName("ConnectorProcessingModeRepository")
public class ConnectorProcessingModeRepositoryTest extends AbstractRepositoryTest {
    private static final String PROCESSING_MODE_UUID = "4f10aed9-2e5f-4780-87f7-5fe1070d5ccf";
    private static final ConnectorBusinessDomainIdentifier DEFAULT_BUSINESS_DOMAIN =
        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier();
    @Autowired
    private ConnectorProcessingModeRepository repository;

    /**
     * Reference data plus a seeded processing mode.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
    })
    private @interface WithProcessingModeData {
    }

    @Nested
    @DisplayName("save a processing mode")
    class Save {
        @Test
        @Sql("classpath:sql/business-domain.sql")
        void should_save_the_processing_mode() {
            var processingMode = ProcessingModeTestFixtures.createWithBusinessDomain();

            var savedProcessingMode = repository.save(
                processingMode, processingMode.businessDomain().identifier()
            );

            assertThat(savedProcessingMode).isNotNull();
        }

        @Test
        void should_fail_when_the_processing_mode_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.save(null, DEFAULT_BUSINESS_DOMAIN)
            );
        }

        @Test
        void should_fail_when_the_business_domain_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.save(ProcessingModeTestFixtures.createWithBusinessDomain(), null)
            );
        }

        @Test
        void should_fail_when_both_arguments_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.save(null, null)
            );
        }
    }

    @Nested
    @DisplayName("find by uuid")
    class FindByUuid {
        @Test
        @WithProcessingModeData
        void should_find_the_processing_mode() {
            var processingMode = repository.findByUuid(PROCESSING_MODE_UUID);

            assertThat(processingMode).isNotNull();
            assertThat(processingMode.uuid()).isNotNull();
        }

        @Test
        void should_return_null_when_the_uuid_is_unknown() {
            var processingMode = repository.findByUuid("unknown-uuid");

            assertThat(processingMode).isNull();
        }

        @Test
        void should_fail_when_the_uuid_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByUuid(null)
            );
        }
    }

    @Nested
    @DisplayName("find by business domain identifier")
    class FindByBusinessDomainIdentifier {
        @Test
        @WithProcessingModeData
        void should_find_the_processing_mode() {
            var processingMode = repository.findByBusinessDomainIdentifier(DEFAULT_BUSINESS_DOMAIN);

            assertThat(processingMode).isNotNull();
            assertThat(processingMode.businessDomain().identifier())
                .isEqualTo(DEFAULT_BUSINESS_DOMAIN);
        }

        @Test
        @WithProcessingModeData
        void should_return_null_when_the_business_domain_is_unknown() {
            var processingMode = repository.findByBusinessDomainIdentifier(
                ConnectorBusinessDomainIdentifier.builder().build()
            );

            assertThat(processingMode).isNull();
        }

        @Test
        void should_fail_when_the_business_domain_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByBusinessDomainIdentifier(null)
            );
        }
    }

    @Nested
    @DisplayName("find all")
    class FindAll {
        @Test
        @WithProcessingModeData
        void should_find_all_the_processing_modes() {
            var processingModes = repository.findAll();

            assertThat(processingModes).isNotNull();
            assertThat(processingModes).hasSize(1);
        }
    }
}
