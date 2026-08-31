/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.container.check.businesscontent;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.infrastructure.outbound.security.container.checks.businesscontent.ConnectorContainerBusinessContentDigestChecker;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainer;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainerBusinessContent;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.outbound.security.util.DigestUtil;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ConnectorContainerBusinessContentDigestChecker")
class ConnectorContainerBusinessContentDigestCheckerTest {
    private static final String ALGORITHM = "SHA-256";
    private static final byte[] BUSINESS_BYTES =
        "<business-content/>".getBytes(StandardCharsets.UTF_8);

    private final ConnectorContainerBusinessContentDigestChecker checker =
        new ConnectorContainerBusinessContentDigestChecker();

    private static DSSDocument businessDocument() {
        return new InMemoryDocument(BUSINESS_BYTES, "business-content.xml");
    }

    private static ConnectorToken tokenWith(byte[] digestValue) {
        var token = mock(ConnectorToken.class, RETURNS_DEEP_STUBS);
        when(token.getDocument().getDigestMethod().getAlgorithm()).thenReturn(
            ConnectorContainerBusinessContentDigestCheckerTest.ALGORITHM);
        when(token.getDocument().getDigestValue()).thenReturn(digestValue);
        return token;
    }

    private static ConnectorContainer containerWith(ConnectorToken token, DSSDocument businessDoc) {
        var businessContent = new ConnectorContainerBusinessContent();
        businessContent.setDocument(businessDoc);
        // tokenXML / tokenPDF / asicDocument are unused by this checker.
        return new ConnectorContainer(businessContent, token, null, null, null);
    }

    private static byte[] expectedDigest(DSSDocument businessDoc) {
        var digestAlgorithm = DigestAlgorithm.forName(ALGORITHM.replace("-", ""));
        return DigestUtil.digest(DSSUtils.toByteArray(businessDoc), digestAlgorithm.toString());
    }

    @Nested
    @DisplayName("digest comparison")
    class DigestComparison {
        @Test
        void should_accept_when_the_computed_digest_matches_the_token_digest() {
            var businessDoc = businessDocument();
            var token = tokenWith(expectedDigest(businessDoc));

            assertThatCode(() -> checker.check(containerWith(token, businessDoc)))
                .doesNotThrowAnyException();
        }

        @Test
        void should_reject_when_the_computed_digest_differs_from_the_token_digest() {
            var businessDoc = businessDocument();
            var token = tokenWith(new byte[]{0, 0, 0});

            assertThatThrownBy(() -> checker.check(containerWith(token, businessDoc)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not match");
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Nested
    @DisplayName("invalid input")
    class InvalidInput {
        @Test
        void should_reject_null_container() {
            assertThatThrownBy(() -> checker.check(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_reject_null_token() {
            assertThatThrownBy(() -> checker.check(containerWith(null, businessDocument())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("token has no document");
        }

        @Test
        void should_reject_token_without_a_document() {
            var token = mock(ConnectorToken.class);
            when(token.getDocument()).thenReturn(null);

            assertThatThrownBy(() -> checker.check(containerWith(token, businessDocument())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("token has no document");
        }

        @Test
        void should_reject_a_missing_digest_method() {
            var token = mock(ConnectorToken.class, RETURNS_DEEP_STUBS);
            when(token.getDocument().getDigestMethod()).thenReturn(null);

            assertThatThrownBy(() -> checker.check(containerWith(token, businessDocument())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not declare a digest algorithm");
        }

        @Test
        void should_reject_a_missing_digest_algorithm() {
            var token = mock(ConnectorToken.class, RETURNS_DEEP_STUBS);
            when(token.getDocument().getDigestMethod().getAlgorithm()).thenReturn(null);

            assertThatThrownBy(() -> checker.check(containerWith(token, businessDocument())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not declare a digest algorithm");
        }
    }
}
