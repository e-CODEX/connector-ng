/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.validation.technical;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.ConnectorMessageDocumentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.infrastructure.outbound.security.token.validation.technical.ConnectorTokenValidationFactory;
import eu.ecodex.connector.infrastructure.security.token.BaseTokenTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("ConnectorSecurityValidationFactory")
public class ConnectorSecurityValidationFactoryTest extends BaseTokenTest {
    @Autowired
    private ConnectorTokenValidationFactory validationFactory;

    @Nested
    @DisplayName("detached signature")
    class DetachedSignature {
        @Test
        void should_provide_default_technical_validation_generator() {
            var message = BusinessMessageTestFixtures.createOutboundMessage();

            var validator = validationFactory.createTechnicalValidation(message);

            assertThat(validator.supportsAuthenticationBased()).isFalse();
        }
    }

    @Nested
    @DisplayName("authentication-based signature")
    class AuthenticationBasedSignature {
        @Test
        void should_provide_aes_technical_validation_generator() {
            var message = BusinessMessageTestFixtures
                .createOutboundMessage()
                .toBuilder()
                .businessContent(
                    MessageContentTestFixtures
                        .createContent()
                        .toBuilder()
                        .businessDocument(
                            ConnectorMessageDocumentTestFixtures
                                .createAuthenticationBasedDocument()
                        )
                        .build()
                )
                .build();

            var validator = validationFactory.createTechnicalValidation(message);

            assertThat(validator.supportsAuthenticationBased()).isTrue();
        }
    }

    @Nested
    @DisplayName("no signature")
    class NoSignature {
        @Test
        void should_provide_aes_technical_validation_generator() {
            // default AES type is set to AUTHENTICATION_BASED in application.properties.
            var message = BusinessMessageTestFixtures
                .createOutboundMessage()
                .toBuilder()
                .businessContent(
                    MessageContentTestFixtures
                        .createContent()
                        .toBuilder()
                        .businessDocument(
                            ConnectorMessageDocumentTestFixtures
                                .createDocumentWithoutSignature()
                        )
                        .build()
                )
                .build();

            var validator = validationFactory.createTechnicalValidation(message);

            assertThat(validator.supportsAuthenticationBased()).isTrue();
        }
    }
}