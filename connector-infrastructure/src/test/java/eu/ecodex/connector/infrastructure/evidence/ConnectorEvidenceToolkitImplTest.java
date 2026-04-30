/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.ConnectorEvidenceToolkit;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("infra")
@SpringBootTest(classes = RemEvidenceTestConfiguration.class)
class ConnectorEvidenceToolkitImplTest {

    @Autowired
    private ConnectorEvidenceToolkit evidenceToolkit;

    @Test
    void submission_acceptance_contains_enveloped_signature_bytes() {
        var base = MessageTestFixtures.createValidOutboundBusinessMessage();
        var as4 = base.as4Properties().toBuilder()
                      .ebmsMessageIdentifier("urn:test:ebms:001")
                      .build();
        ConnectorMessage message = base.toBuilder().as4Properties(as4).build();

        var evidence = evidenceToolkit.create(
                message,
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                null
        );

        assertThat(evidence.content()).isNotNull();
        assertThat(evidence.content().length).isPositive();
        String asUtf8 = new String(evidence.content(), java.nio.charset.StandardCharsets.UTF_8);
        assertThat(asUtf8).contains("ds:Signature");
    }
}
