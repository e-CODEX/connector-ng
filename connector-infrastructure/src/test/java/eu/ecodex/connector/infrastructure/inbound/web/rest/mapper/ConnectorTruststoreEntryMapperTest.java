/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.mapper;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import eu.ecodex.connector.domain.model.security.KeystoreType;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorCertificateInfoDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


@DisplayName("ConnectorTruststoreEntryMapper")
public class ConnectorTruststoreEntryMapperTest {
    private static ConnectorTruststore truststoreFrom() {
        return new ConnectorTruststore(
            "keystore.jks",
            FileTestFixtures.readAsBytes("keystores/connector-truststore.jks"),
            "12345",
            KeystoreType.JKS
        );
    }

    @Nested
    @DisplayName("when the truststore is valid")
    class WhenTruststoreIsValid {
        @Test
        void should_map_the_certificate_fields() {
            var dto = ConnectorTruststoreEntryMapper.toEntries(truststoreFrom()).getFirst();

            assertThat(dto.alias()).isEqualTo("connector_blue");
            assertThat(dto.subject()).contains("CN=connector_blue,C=BL");
            assertThat(dto.issuer()).contains("CN=connector_blue,C=BL");
            assertThat(dto.signatureAlgorithm()).isEqualTo("SHA256withRSA");
            assertThat(dto.keyAlgorithm()).isEqualTo("RSA");
        }

        @Test
        void should_mark_the_entries_as_trusted_certificates() {
            assertThat(ConnectorTruststoreEntryMapper.toEntries(truststoreFrom()))
                .hasSize(2)
                .extracting(ConnectorCertificateInfoDto::entryType)
                .isEqualTo(List.of("TRUSTED_CERTIFICATE", "TRUSTED_CERTIFICATE"));
        }

        @Test
        void should_return_all_the_entries() {
            assertThat(ConnectorTruststoreEntryMapper.toEntries(truststoreFrom()))
                .extracting(ConnectorCertificateInfoDto::alias)
                .containsExactlyInAnyOrder("connector_blue", "connector_red");
        }
    }

    @Nested
    @DisplayName("when there is nothing to map")
    class WhenNothingToMap {
        @Test
        void should_return_an_empty_list_when_the_truststore_is_null() {
            assertThat(ConnectorTruststoreEntryMapper.toEntries(null)).isEmpty();
        }

        @Test
        void should_return_an_empty_list_when_the_content_is_null() {
            var truststore = truststoreFrom().toBuilder().content(null).build();

            assertThat(ConnectorTruststoreEntryMapper.toEntries(truststore)).isEmpty();
        }
    }
}
