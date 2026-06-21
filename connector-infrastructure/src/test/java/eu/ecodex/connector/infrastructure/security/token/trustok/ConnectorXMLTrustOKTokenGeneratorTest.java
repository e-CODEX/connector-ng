/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.token.trustok;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.infrastructure.security.SecurityUtil;
import eu.ecodex.connector.infrastructure.security.token.BaseTokenTest;
import eu.ecodex.connector.infrastructure.security.token.trustok.xml.ConnectorXMLTrustOKTokenGenerator;
import eu.ecodex.connector.infrastructure.security.token.validation.ConnectorTokenValidationGenerator;
import eu.ecodex.connector.infrastructure.security.token.validation.technical.ConnectorTokenValidationFactory;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectorXMLTrustOKTokenGeneratorTest extends BaseTokenTest {
    @Autowired
    private ConnectorXMLTrustOKTokenGenerator trustOKTokenGenerator;
    @Autowired
    private ConnectorTokenValidationGenerator validationGenerator;
    @Autowired
    private ConnectorTokenValidationFactory validationFactory;

    @Test
    void should_create_xml_trust_ok_token_and_sign_it_successfully() throws IOException {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var document = FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf");
        var businessDocument = new InMemoryDocument(document);
        var issuer = validationFactory.getTokenIssuer(message);

        var token = validationGenerator.createToken(
                message,
                businessDocument,
                null,
                issuer
        );

        var xmlToken = trustOKTokenGenerator.generate(token);

        assertThat(xmlToken).isNotNull();
        assertThat(xmlToken.getMimeType()).isEqualTo(MimeTypeEnum.XML);
        assertThat(SecurityUtil.hasSignature(xmlToken)).isTrue();
    }
}
