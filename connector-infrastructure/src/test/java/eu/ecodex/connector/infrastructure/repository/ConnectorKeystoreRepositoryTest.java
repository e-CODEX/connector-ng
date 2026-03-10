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
import eu.ecodex.connector.KeystoreTestFixtures;
import eu.ecodex.connector.RepositoryContextConfiguration;
import eu.ecodex.connector.domain.spi.ConnectorKeystoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(classes = RepositoryContextConfiguration.class)
@SuppressWarnings({"checkstyle:MissingJavadocType", "DataFlowIssue", "checkstyle:LineLength"})
public class ConnectorKeystoreRepositoryTest {
    @Autowired
    private ConnectorKeystoreRepository repository;

    @Test
    @Sql("classpath:sql/business-domain.sql")
    @Sql("classpath:sql/processing-mode.sql")
    void should_save_keystore_to_database_successfully() {
        var keystore = KeystoreTestFixtures.createKeystore();

        var savedKeystore = repository.save(
                keystore,
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(savedKeystore).isNotNull();
        assertThat(savedKeystore.uuid()).isNotNull();
        assertThat(savedKeystore.content()).isNotNull();
        assertThat(savedKeystore.password()).isEqualTo(keystore.password());
        assertThat(savedKeystore.description()).isEqualTo(keystore.description());
        assertThat(savedKeystore.createdAt()).isNotNull();
        assertThat(savedKeystore.updatedAt()).isNotNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_keystore_without_business_domain_identifier() {
        var keystore = KeystoreTestFixtures.createKeystore();

        assertThrows(
                NullPointerException.class,
                () -> repository.save(keystore, null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_keystore_without_keystore2() {
        var businessDomainIdentifier = BusinessDomainIdentifierTestFixtures
                .createDefaultBusinessDomainIdentifier();

        assertThrows(
                NullPointerException.class,
                () -> repository.save(null, businessDomainIdentifier)
        );
    }
}
