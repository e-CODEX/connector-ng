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

import eu.ecodex.connector.RepositoryContextConfiguration;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.spi.ConnectorLinkPartnerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = RepositoryContextConfiguration.class,
        properties = {
                """
                        connector.link.gateway.link-config.config-name=default
                        
                        connector.link.gateway.link-config.properties.endpoint.key-store.path=file:./config/keystores/gwlink-keystore.jks
                        connector.link.gateway.link-config.properties.endpoint.key-store.password=*****
                        connector.link.gateway.link-config.properties.endpoint.private-key.alias=gw_blue
                        connector.link.gateway.link-config.properties.endpoint.private-key.password=*****
                        
                        connector.link.gateway.link-config.properties.endpoint.trust-store.path=file:./config/keystores/gwlink-truststore.jks
                        connector.link.gateway.link-config.properties.endpoint.trust-store.password=*****
                        connector.link.gateway.link-config.properties.endpoint.encrypt-alias=gw_blue
                        
                        connector.link.gateway.link-config.properties.logging-enabled=true
                        
                        connector.link.gateway.link-partners[0].name=default_gateway
                        connector.link.gateway.link-partners[0].description=blue gateway
                        connector.link.gateway.link-partners[0].enabled=true
                        connector.link.gateway.link-partners[0].receiver-mode=push
                        connector.link.gateway.link-partners[0].sender-mode=push
                        
                        # backends
                        connector.link.backend[0].link-config.name=default_backend_config
                        
                        connector.link.backend[0].link-config.properties.endpoint.key-store.path=file:config/keystores/backend-keystore.jks
                        connector.link.backend[0].link-config.properties.endpoint.key-store.password=*****
                        connector.link.backend[0].link-config.properties.endpoint.private-key.alias=connector_blue
                        connector.link.backend[0].link-config.properties.endpoint.private-key.password=*****
                        
                        connector.link.backend[0].link-config.properties.endpoint.trust-store.path=file:config/keystores/backend-truststore.jks
                        connector.link.backend[0].link-config.properties.endpoint.trust-store.password=*****
                        connector.link.backend[0].link-config.properties.endpoint.encrypt-alias=alice
                        
                        connector.link.backend[0].link-config.properties.logging-enabled=true
                        
                        # link partner 0 specific configuration
                        # this name must match any message routing config
                        # this name will also be stored into the DB to the specific message as its backend name
                        connector.link.backend[0].link-partners[0].name=backend_alice
                        connector.link.backend[0].link-partners[0].description=backend alice
                        connector.link.backend[0].link-partners[0].enabled=true
                        # this link partner pushes a message to the connector (connector is passive in receiver receiveMode)
                        connector.link.backend[0].link-partners[0].receiver-mode=passive
                        #this linkPartner operates in push receiveMode (connector pushes new messages to backend)
                        connector.link.backend[0].link-partners[0].sender-mode=push
                        # this must match the certificate alias within the trust-store
                        connector.link.backend[0].link-partners[0].properties.encryption-alias=alice
                        # this must match the certificate DN (lower- or UPPERcase is ignored)
                        connector.link.backend[0].link-partners[0].properties.certificate-dn=cn=alice
                        """
        }
)
public class ConnectorLinkPartnerRepositoryTest {
    @Autowired
    private ConnectorLinkPartnerRepository repository;

    @Test
    void should_load_link_partners_from_properties_successfully() {
        var partners = repository.findAll();

        assertThat(partners).isNotNull();
        assertThat(partners).hasSize(2);
    }

    @Test
    void should_find_link_partner_by_name_successfully() {
        var name = ConnectorLinkPartnerName.builder().name("default_gateway").build();

        var partner = this.repository.findByName(name);

        assertThat(partner).isNotNull();
        assertThat(partner.name()).isEqualTo(name);
    }
}
