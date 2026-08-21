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

import eu.ecodex.connector.RepositoryContextConfiguration;
import eu.ecodex.connector.application.port.spi.link.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("DataFlowIssue")
@DisplayName("ConnectorLinkPartnerRepository")
@SpringBootTest(
    classes = RepositoryContextConfiguration.class,
    properties = {
        """
                # backends
                connector.link.backend[0].link-config.name=default_backend_config
                connector.link.backend[0].link-config.properties.endpoint.keystore.path=file:config/keystores/backend-keystore.jks
                connector.link.backend[0].link-config.properties.endpoint.keystore.password=*****
                connector.link.backend[0].link-config.properties.endpoint.private-key.alias=connector_blue
                connector.link.backend[0].link-config.properties.endpoint.private-key.password=*****
                connector.link.backend[0].link-config.properties.endpoint.truststore.path=file:config/keystores/backend-truststore.jks
                connector.link.backend[0].link-config.properties.endpoint.truststore.password=*****
                connector.link.backend[0].link-config.properties.endpoint.encrypt-alias=alice
                connector.link.backend[0].link-config.properties.logging-enabled=true
                # link partner 0 specific configuration
                # this name must match any message routing config
                # this name will also be stored into the DB to the specific message as its backend name
                connector.link.backend[0].link-partners[0].name=backend_alice
                connector.link.backend[0].link-partners[0].description=backend alice
                connector.link.backend[0].link-partners[0].enabled=true
                #this linkPartner operates in push receiveMode (connector pushes new messages to backend)
                connector.link.backend[0].link-partners[0].sender-mode=push
                # this must match the certificate alias within the trust-store
                connector.link.backend[0].link-partners[0].properties.encryption-alias=alice
                # this must match the certificate DN (lower- or UPPERcase is ignored)
                connector.link.backend[0].link-partners[0].properties.certificate-dn=cn=alice
                spring.autoconfigure.exclude=org.apache.cxf.spring.boot.autoconfigure.micrometer.MicrometerMetricsAutoConfiguration
            """
    }
)
public class ConnectorLinkPartnerRepositoryTest {
    @Autowired
    private ConnectorLinkPartnerRepository repository;

    @Nested
    @DisplayName("find all")
    class FindAll {
        @Test
        void should_load_all_the_link_partners_from_properties() {
            var partners = repository.findAll();

            assertThat(partners).isNotNull();
            assertThat(partners).hasSize(2);
        }
    }

    @Nested
    @DisplayName("find by name")
    class FindByName {
        @Test
        void should_find_the_link_partner() {
            var name = ConnectorLinkPartnerName.builder().name("backend_alice").build();

            var partner = repository.findByName(name);

            assertThat(partner).isNotNull();
            assertThat(partner.name()).isEqualTo(name);
        }

        @Test
        void should_throw_when_the_name_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByName(null)
            );
        }
    }

    @Nested
    @DisplayName("find by certificate DN")
    class FindByCertificateDn {
        @Test
        void should_find_the_link_partner() {
            var partner = repository.findByCertificateDn("cn=alice");

            assertThat(partner).isNotNull();
            assertThat(partner.name().name()).isEqualTo("backend_alice");
        }

        @Test
        void should_throw_when_the_certificate_dn_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByCertificateDn(null)
            );
        }
    }
}
