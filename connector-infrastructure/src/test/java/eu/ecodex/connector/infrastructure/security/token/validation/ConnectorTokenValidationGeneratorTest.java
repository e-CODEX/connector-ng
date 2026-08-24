/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.validation;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.infrastructure.outbound.security.token.validation.ConnectorTokenValidationGenerator;
import eu.ecodex.connector.infrastructure.outbound.security.token.validation.technical.ConnectorTokenValidationFactory;
import eu.ecodex.connector.infrastructure.security.token.BaseTokenTest;
import eu.europa.esig.dss.model.InMemoryDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("ConnectorTokenValidationGenerator")
public class ConnectorTokenValidationGeneratorTest extends BaseTokenTest {
    @Autowired
    private ConnectorTokenValidationGenerator validationGenerator;
    @Autowired
    private ConnectorTokenValidationFactory validationFactory;

    @Test
    void should_generate_valid_token() {
        var message = BusinessMessageTestFixtures.createOutboundMessage();
        var document = FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf");
        var businessDocument = new InMemoryDocument(document);
        var issuer = validationFactory.getTokenIssuer(message);

        var token = validationGenerator.createToken(
            message,
            businessDocument,
            null,
            issuer
        );

        assertThat(token).isNotNull();
        assertThat(token.getIssuer()).isNotNull();
        assertThat(token.getIssuer()).isEqualTo(issuer);
        assertThat(token.getValidation().getLegalResult()).isNotNull();
        assertThat(token.getValidation().getLegalResult()).isNotNull();
    }
}
