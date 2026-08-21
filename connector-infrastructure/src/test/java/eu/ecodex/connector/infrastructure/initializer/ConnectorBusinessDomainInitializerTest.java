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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorBusinessDomainAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeException;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorListBusinessDomain;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorRegisterBusinessDomain;
import eu.ecodex.connector.application.port.api.pmode.ConnectorRegisterProcessingMode;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.model.security.KeystoreType;
import eu.ecodex.connector.infrastructure.outbound.adapter.exception.ConnectorProcessingModeParsingException;
import eu.ecodex.connector.infrastructure.property.businessdomain.ConnectorBusinessDomainProperties;
import eu.ecodex.connector.infrastructure.property.businessdomain.DefaultBusinessDomainPmodeProperties;
import eu.ecodex.connector.infrastructure.property.businessdomain.DefaultBusinessDomainProperties;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.io.DefaultResourceLoader;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorBusinessDomainInitializer")
public class ConnectorBusinessDomainInitializerTest {
    private static final String P_MODE_CONTENT = "<configuration party=\"blue_gw\"/>";
    private static final byte[] TRUSTSTORE_CONTENT =
        "keystore-bytes".getBytes(StandardCharsets.UTF_8);
    private static final String TRUSTSTORE_PASSWORD = "changeit";

    @Mock
    private ConnectorRegisterBusinessDomain registerBusinessDomainService;
    @Mock
    private ConnectorListBusinessDomain listBusinessDomainService;
    @Mock
    private ConnectorRegisterProcessingMode registerProcessingModeService;
    @Mock
    private ConnectorBusinessDomainProperties domainProperties;
    @Mock
    private ApplicationArguments applicationArguments;

    private ConnectorBusinessDomainInitializer initializer;

    private static DefaultBusinessDomainProperties properties(
        String identifier, String description, boolean enabled) {
        var props = new DefaultBusinessDomainProperties();
        props.setIdentifier(identifier);
        props.setDescription(description);
        props.setEnabled(enabled);
        props.setPmode(new DefaultBusinessDomainPmodeProperties());

        return props;
    }

    private static DefaultBusinessDomainProperties withProcessingMode(Path tempDir)
        throws IOException {
        var processingModeFile = tempDir.resolve("pmode.xml");
        var truststoreFile = tempDir.resolve("truststore.jks");
        Files.writeString(processingModeFile, P_MODE_CONTENT);
        Files.write(truststoreFile, TRUSTSTORE_CONTENT);

        var props = properties("domain-a", "Domain " + "domain-a", true);
        props.getPmode().setFile("file:" + processingModeFile);
        props.getPmode().setTruststore("file:" + truststoreFile);
        props.getPmode().setTruststorePassword(TRUSTSTORE_PASSWORD);

        return props;
    }

    @BeforeEach
    void setUp() {
        initializer = new ConnectorBusinessDomainInitializer(
            registerBusinessDomainService,
            listBusinessDomainService,
            registerProcessingModeService,
            domainProperties,
            new DefaultResourceLoader()
        );
    }

    @Nested
    @DisplayName("when no default business domain is configured")
    class NoDefaultsConfigured {
        @ParameterizedTest
        @NullSource
        @EmptySource
        void should_register_the_built_in_default_when_none_is_registered_yet(
            List<DefaultBusinessDomainProperties> defaults) {
            when(domainProperties.getDefaults()).thenReturn(defaults);
            when(listBusinessDomainService.execute()).thenReturn(List.of());

            initializer.run(applicationArguments);

            verify(registerBusinessDomainService)
                .execute(ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN);

            verifyNoInteractions(registerProcessingModeService);
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        void should_do_nothing_when_a_business_domain_is_already_registered(
            List<DefaultBusinessDomainProperties> defaults) {
            when(domainProperties.getDefaults()).thenReturn(defaults);
            when(listBusinessDomainService.execute())
                .thenReturn(List.of(mock(ConnectorBusinessDomain.class)));

            initializer.run(applicationArguments);

            verifyNoInteractions(registerBusinessDomainService);
            verifyNoInteractions(registerProcessingModeService);
        }
    }

    @Nested
    @DisplayName("when default business domains are configured")
    class DefaultsConfigured {
        @Test
        void should_register_every_configured_domain() {
            when(domainProperties.getDefaults()).thenReturn(List.of(
                properties("domain-a", "Domain A", true),
                properties("domain-b", "Domain B", false)
            ));

            initializer.run(applicationArguments);

            verify(registerBusinessDomainService).execute(
                argThat(domain ->
                            "domain-a".equals(domain.identifier().messageLaneIdentifier())
                                && "Domain A".equals(domain.description())
                                && domain.enabled()
                                && domain.source() == ConnectorConfigurationSource.IMPLEMENTATION)
            );

            verify(registerBusinessDomainService).execute(
                argThat(domain -> "domain-b".equals(domain.identifier().messageLaneIdentifier())
                    && !domain.enabled())
            );

            verify(registerBusinessDomainService, times(2)).execute(any());
            verifyNoInteractions(listBusinessDomainService);
        }

        @Test
        void should_skip_a_domain_that_is_already_registered_and_continue_with_the_others() {
            when(domainProperties.getDefaults()).thenReturn(List.of(
                properties("domain-a", "Domain A", true),
                properties("domain-b", "Domain B", true),
                properties("domain-c", "Domain C", true)
            ));

            doAnswer(invocation -> {
                ConnectorBusinessDomain domain = invocation.getArgument(0);
                if ("domain-b".equals(domain.identifier().messageLaneIdentifier())) {
                    throw new ConnectorBusinessDomainAlreadyExistsException("already there");
                }
                return null;
            }).when(registerBusinessDomainService).execute(any());

            assertThatNoException().isThrownBy(() -> initializer.run(applicationArguments));

            verify(registerBusinessDomainService, times(3)).execute(any());
        }

        @Test
        void should_fail_startup_when_a_domain_registration_fails_unexpectedly() {
            when(domainProperties.getDefaults())
                .thenReturn(List.of(properties("domain-a", "Domain A", true)));

            doThrow(new RuntimeException("Database unreachable"))
                .when(registerBusinessDomainService).execute(any());

            assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> initializer.run(applicationArguments))
                .withMessageContaining("Database unreachable");
        }

        @Test
        void should_not_register_a_processing_mode_when_none_is_configured() {
            when(domainProperties.getDefaults())
                .thenReturn(List.of(properties("domain-a", "Domain A", true)));

            initializer.run(applicationArguments);

            verifyNoInteractions(registerProcessingModeService);
        }
    }

    @Nested
    @DisplayName("when a processing mode is configured")
    class ProcessingModeConfigured {
        @Test
        void should_register_the_processing_mode_with_its_content_and_truststore(
            @TempDir Path tempDir)
            throws IOException {
            when(domainProperties.getDefaults())
                .thenReturn(List.of(withProcessingMode(tempDir)));

            initializer.run(applicationArguments);

            ArgumentCaptor<ConnectorBusinessDomainIdentifier> identifier = ArgumentCaptor.captor();
            ArgumentCaptor<ConnectorProcessingMode> processingMode = ArgumentCaptor.captor();
            verify(registerProcessingModeService)
                .execute(identifier.capture(), processingMode.capture());

            assertThat(identifier.getValue().messageLaneIdentifier()).isEqualTo("domain-a");

            var registered = processingMode.getValue();
            assertThat(registered.content()).isEqualTo(P_MODE_CONTENT);
            assertThat(registered.filename()).isEqualTo("pmode.xml");
            assertThat(registered.description()).contains("domain-a");

            assertThat(registered.truststore()).satisfies(truststore -> {
                assertThat(truststore.filename()).isEqualTo("truststore.jks");
                assertThat(truststore.password()).isEqualTo(TRUSTSTORE_PASSWORD);
                assertThat(truststore.content()).isEqualTo(TRUSTSTORE_CONTENT);
                assertThat(truststore.type()).isEqualTo(KeystoreType.JKS);
            });
        }

        @Test
        void should_register_the_domain_before_its_processing_mode(@TempDir Path tempDir)
            throws IOException {
            when(domainProperties.getDefaults())
                .thenReturn(List.of(withProcessingMode(tempDir)));

            initializer.run(applicationArguments);

            var inOrder = inOrder(
                registerBusinessDomainService, registerProcessingModeService);
            inOrder.verify(registerBusinessDomainService).execute(any());
            inOrder.verify(registerProcessingModeService).execute(any(), any());
        }

        @Test
        void should_skip_a_processing_mode_that_is_already_registered(@TempDir Path tempDir)
            throws IOException {
            when(domainProperties.getDefaults())
                .thenReturn(List.of(withProcessingMode(tempDir)));

            doThrow(new ConnectorProcessingModeException("already linked"))
                .when(registerProcessingModeService).execute(any(), any());

            // Restarting the connector must not abort the application context.
            assertThatNoException().isThrownBy(() -> initializer.run(applicationArguments));

            verify(registerBusinessDomainService).execute(any());
            verify(registerProcessingModeService).execute(any(), any());
        }

        @Test
        void should_fail_startup_when_the_processing_mode_file_does_not_exist(@TempDir Path tempDir)
            throws IOException {
            var props = withProcessingMode(tempDir);
            props.getPmode().setFile("file:" + tempDir.resolve("does-not-exist.xml"));
            when(domainProperties.getDefaults()).thenReturn(List.of(props));

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> initializer.run(applicationArguments))
                .withMessageContaining("domain-a")
                .withRootCauseInstanceOf(java.io.FileNotFoundException.class);

            verifyNoInteractions(registerProcessingModeService);
        }

        @Test
        void should_fail_startup_when_the_truststore_is_not_configured(@TempDir Path tempDir)
            throws IOException {
            var props = withProcessingMode(tempDir);
            props.getPmode().setTruststore(null);
            when(domainProperties.getDefaults()).thenReturn(List.of(props));

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> initializer.run(applicationArguments))
                .withMessageContaining("truststore");

            verifyNoInteractions(registerProcessingModeService);
        }

        @Test
        void should_fail_startup_when_the_truststore_type_cannot_be_determined(
            @TempDir Path tempDir)
            throws IOException {
            var truststoreFile = tempDir.resolve("truststore.unknown");
            Files.write(truststoreFile, TRUSTSTORE_CONTENT);

            var props = withProcessingMode(tempDir);
            props.getPmode().setTruststore("file:" + truststoreFile);
            when(domainProperties.getDefaults()).thenReturn(List.of(props));

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> initializer.run(applicationArguments))
                .withMessageContaining("truststore.unknown");
        }

        @Test
        void should_fail_startup_when_the_processing_mode_is_rejected(@TempDir Path tempDir)
            throws IOException {
            when(domainProperties.getDefaults())
                .thenReturn(List.of(withProcessingMode(tempDir)));

            doThrow(new ConnectorProcessingModeParsingException("malformed definition"))
                .when(registerProcessingModeService).execute(any(), any());

            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> initializer.run(applicationArguments));
        }
    }

    @Nested
    @DisplayName("filename extraction")
    class FilenameExtraction {
        @ParameterizedTest
        @CsvSource({
            "file:/opt/connector/pmode.xml,           pmode.xml",
            "classpath:pmode/pmode.xml,               pmode.xml",
            "https://host/config/pmode.xml,           pmode.xml",
            "https://host/config/pmode.xml?version=2, pmode.xml",
            "https://host/config/pmode.xml#section,   pmode.xml",
            "file:C:\\connector\\pmode.xml,           pmode.xml",
            "pmode.xml,                               pmode.xml"
        })
        void should_extract_the_filename_from_a_location(String location, String expected) {
            assertThat(ConnectorBusinessDomainInitializer.filenameOf(location)).isEqualTo(expected);
        }
    }
}
