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
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.ecodex.connector.infrastructure.outbound.security.token.validation.technical.ConnectorTokenAuthBasedTechnicalValidationGenerator;
import eu.ecodex.connector.infrastructure.property.businessdocument.ConnectorBusinessDocumentProperties;
import eu.ecodex.connector.infrastructure.security.token.BaseTokenTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("ConnectorTokenAuthBasedTechnicalValidationGenerator")
public class ConnectorTokenAuthBasedTechnicalValidationGeneratorTest extends BaseTokenTest {
    @Autowired
    private ConnectorBusinessDocumentProperties businessDocumentProperties;

    @Test
    void should_build_valid_validation() throws Exception {
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

        var generator = new ConnectorTokenAuthBasedTechnicalValidationGenerator(
            businessDocumentProperties.getAuthenticationValidation().getIdentityProvider(),
            message
        );

        var validation = generator.generate(null, null);
        assertThat(validation).isNotNull();

        var authData = validation.getVerificationData().getAuthenticationData();
        assertThat(authData).isNotNull();
        assertThat(authData.getIdentityProvider())
            .isEqualTo(businessDocumentProperties.getAuthenticationValidation()
                                                 .getIdentityProvider());
        assertThat(authData.getUsernameSynonym()).isEqualTo(message.as4Properties()
                                                                   .originalSender());

        var technicalResult = validation.getTechnicalResult();
        assertThat(technicalResult).isNotNull();
        assertThat(technicalResult.getTrustLevel())
            .isEqualTo(ConnectorTokenTechnicalTrustLevel.SUCCESSFUL);
        assertThat(technicalResult.getComment()).isEqualTo("The authentication is valid.");
    }
}
