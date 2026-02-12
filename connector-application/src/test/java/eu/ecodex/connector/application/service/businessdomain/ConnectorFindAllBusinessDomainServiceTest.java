/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.businessdomain;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.application.service.impl.businessdomain.ConnectorListBusinessDomainService;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorFindAllBusinessDomainServiceTest {
    @Mock
    private ConnectorBusinessDomainRepository businessDomainRepository;
    @InjectMocks
    private ConnectorListBusinessDomainService connectorBusinessDomainService;

    @Test
    void should_find_all_business_domains_successfully() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

        when(businessDomainRepository.findAll()).thenReturn(List.of(businessDomain));

        var foundBusinessDomains = connectorBusinessDomainService.execute();

        assertThat(foundBusinessDomains).isNotNull();
        assertThat(foundBusinessDomains).hasSize(1);
    }
}
