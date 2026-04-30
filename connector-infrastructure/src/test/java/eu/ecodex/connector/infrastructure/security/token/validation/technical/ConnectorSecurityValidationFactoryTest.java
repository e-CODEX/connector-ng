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

import eu.ecodex.connector.ConnectorMessageDocumentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.infrastructure.security.token.BaseTokenTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectorSecurityValidationFactoryTest extends BaseTokenTest {
    @Autowired
    private ConnectorTokenValidationFactory validationFactory;

    @Test
    void should_provide_default_technical_validation_generator_if_business_document_has_detached_signature() {
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        var validator = validationFactory.createTechnicalValidation(message);

        assertThat(validator.supportsAuthenticationBased()).isFalse();
    }

    @Test
    void should_provide_aes_technical_validation_generator_if_business_document_has_auth_based_signature() {
        var message = MessageTestFixtures
                .createValidOutboundBusinessMessage()
                .toBuilder()
                .businessContent(
                        MessageContentTestFixtures
                                .createContent()
                                .toBuilder()
                                .businessDocument(
                                        ConnectorMessageDocumentTestFixtures
                                                .createDocumentWithAuthBasedSignature()
                                )
                                .build()
                )
                .build();
        var validator = validationFactory.createTechnicalValidation(message);

        assertThat(validator.supportsAuthenticationBased()).isTrue();
    }

    @Test
    void should_provide_aes_technical_validation_generator_if_no_signature_is_present() {
        // default AES type is set to AUTHENTICATION_BASED in the application.properties
        var message = MessageTestFixtures
                .createValidOutboundBusinessMessage()
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
