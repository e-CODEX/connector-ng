/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.initializer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.service.usecase.businessdomain.ConnectorListBusinessDomain;
import eu.ecodex.connector.application.service.usecase.businessdomain.ConnectorRegisterBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.infrastructure.property.businessdomain.ConnectorBusinessDomainProperties;
import eu.ecodex.connector.infrastructure.property.businessdomain.DefaultBusinessDomainProperties;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

@ExtendWith(MockitoExtension.class)
public class ConnectorBusinessDomainInitializerTest {
    @Mock
    private ConnectorRegisterBusinessDomain registerBusinessDomainService;
    @Mock
    private ConnectorListBusinessDomain listBusinessDomainService;
    @Mock
    private ConnectorBusinessDomainProperties domainProperties;
    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private ConnectorBusinessDomainInitializer initializer;

    // No defaults configured, no existing domains — registers DEFAULT

    @Test
    void should_register_default_in_app_business_domain_if_no_one_is_configured() throws Exception {
        when(domainProperties.getDefaults()).thenReturn(null);
        when(listBusinessDomainService.execute()).thenReturn(List.of());

        initializer.run(applicationArguments);

        verify(registerBusinessDomainService).execute(
                ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN
        );
    }

    @Test
    void should_register_default_in_app_business_domain_if_no_one_is_configured_2()
            throws Exception {
        when(domainProperties.getDefaults()).thenReturn(List.of());
        when(listBusinessDomainService.execute()).thenReturn(List.of());

        initializer.run(applicationArguments);

        verify(registerBusinessDomainService).execute(
                ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN
        );
    }

    // No defaults configured, existing domains found — no-op

    @Test
    void should_do_nothing_if_no_default_business_domain_is_configured_but_one_is_already_registered()
            throws Exception {
        when(domainProperties.getDefaults()).thenReturn(null);
        when(listBusinessDomainService.execute())
                .thenReturn(List.of(mock(ConnectorBusinessDomain.class)));

        initializer.run(applicationArguments);

        verify(registerBusinessDomainService, never()).execute(any());
    }

    @Test
    void should_do_nothing_if_no_default_business_domain_is_configured_but_one_is_already_registered_2()
            throws Exception {
        when(domainProperties.getDefaults()).thenReturn(List.of());
        when(listBusinessDomainService.execute())
                .thenReturn(List.of(mock(ConnectorBusinessDomain.class)));

        initializer.run(applicationArguments);

        verify(registerBusinessDomainService, never()).execute(any());
    }

    // Defaults configured — registers each, skips listBusinessDomainService

    @Test
    void should_register_each_configured_default_business_domains_successfully() throws Exception {
        var props1 = defaultDomainProperties("domain-a", "Domain A", true);
        var props2 = defaultDomainProperties("domain-b", "Domain B", false);
        when(domainProperties.getDefaults()).thenReturn(List.of(props1, props2));

        initializer.run(applicationArguments);

        verify(registerBusinessDomainService).execute(
                argThat(domain ->
                                "domain-a".equals(domain.identifier().messageLaneIdentifier())
                                        && "Domain A".equals(domain.description())
                                        && domain.enabled()
                                        && domain.source() == ConnectorConfigurationSource.IMPLEMENTATION
                ));
        verify(registerBusinessDomainService).execute(
                argThat(domain ->
                                "domain-b".equals(domain.identifier().messageLaneIdentifier())
                                        && "Domain B".equals(domain.description())
                                        && !domain.enabled()
                ));
        verify(registerBusinessDomainService, times(2)).execute(any());
        verifyNoInteractions(listBusinessDomainService);
    }

    // Defaults configured — one fails, others still processed

    @Test
    void should_register_each_configured_default_business_domains_even_if_one_already_exists_successfully() {
        var props1 = defaultDomainProperties("domain-a", "Domain A", true);
        var props2 = defaultDomainProperties("domain-b", "Domain B", true);
        var props3 = defaultDomainProperties("domain-c", "Domain C", true);
        when(domainProperties.getDefaults()).thenReturn(List.of(props1, props2, props3));

        doThrow(new RuntimeException("duplicate identifier"))
                .when(registerBusinessDomainService)
                .execute(argThat(domain ->
                                         "domain-b".equals(domain.identifier()
                                                                 .messageLaneIdentifier())
                ));

        assertThatNoException().isThrownBy(() -> initializer.run(applicationArguments));

        verify(registerBusinessDomainService).execute(
                argThat(domain ->
                                "domain-a".equals(domain.identifier().messageLaneIdentifier())
                ));
        verify(registerBusinessDomainService).execute(
                argThat(domain ->
                                "domain-c".equals(domain.identifier().messageLaneIdentifier())
                ));
        verify(registerBusinessDomainService, times(3)).execute(any());
    }


    private DefaultBusinessDomainProperties defaultDomainProperties(
            String identifier, String description, boolean enabled) {
        var props = mock(DefaultBusinessDomainProperties.class);
        when(props.getIdentifier()).thenReturn(identifier);
        when(props.getDescription()).thenReturn(description);
        when(props.isEnabled()).thenReturn(enabled);

        return props;
    }
}
