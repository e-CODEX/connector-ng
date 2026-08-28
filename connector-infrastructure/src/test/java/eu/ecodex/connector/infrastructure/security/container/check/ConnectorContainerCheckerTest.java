/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.container.check;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.infrastructure.outbound.security.container.checks.ConnectorContainerChecker;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainer;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainerBusinessContent;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenAESType;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("DataFlowIssue")
@DisplayName("ConnectorContainerChecker")
class ConnectorContainerCheckerTest {
    private final ConnectorContainerChecker checker = new ConnectorContainerChecker();

    private static DSSDocument nonEmptyDoc(String name) {
        return new InMemoryDocument(new byte[]{1, 2, 3}, name);
    }

    private static DSSDocument emptyDoc(String name) {
        return new InMemoryDocument(new byte[0], name);
    }

    private static Arguments caze(String name, Consumer<Fixture> mutation, String expectedMessage) {
        return Arguments.of(name, mutation, expectedMessage);
    }

    static final class Fixture {
        DSSDocument asicDocument = nonEmptyDoc("asic.zip");
        DSSDocument businessDocument = nonEmptyDoc("business.xml");
        DSSDocument detachedSignature = nonEmptyDoc("signature.p7s");
        DSSDocument tokenPdf = nonEmptyDoc("token.pdf");
        DSSDocument tokenXml = nonEmptyDoc("token.xml");
        ConnectorToken token = mock(ConnectorToken.class, RETURNS_DEEP_STUBS);

        Fixture() {
            when(token.getDocument().getDigestValue()).thenReturn(new byte[]{1, 2, 3});
            when(token.getDocument().getFilename()).thenReturn("token.xml");
            when(token.getDocument().getType()).thenReturn("application/pdf");
            when(token.getValidation().getTechnicalResult().getTrustLevel())
                .thenReturn(ConnectorTokenTechnicalTrustLevel.SUCCESSFUL);
            when(token.getAdvancedElectronicSystem())
                .thenReturn(ConnectorTokenAESType.AUTHENTICATION_BASED);
        }

        ConnectorContainer build() {
            var businessContent = new ConnectorContainerBusinessContent();
            businessContent.setDocument(businessDocument);
            businessContent.setDetachedSignature(detachedSignature);
            return new ConnectorContainer(businessContent, token, tokenXml, tokenPdf, asicDocument);
        }
    }

    @Nested
    @DisplayName("valid containers")
    class Accepted {
        @Test
        void should_accept_a_fully_valid_container() {
            var fixture = new Fixture();
            when(fixture.token.getValidationVerificationData()
                              .getAuthenticationData()
                              .getUsernameSynonym())
                .thenReturn("user");
            when(fixture.token.getValidationVerificationData()
                              .getAuthenticationData()
                              .getIdentityProvider())
                .thenReturn("identity-provider");
            assertThatCode(() -> checker.check(fixture.build()))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("rejected containers")
    class Rejected {
        static Stream<Arguments> invalidCases() {
            return Stream.of(
                // documents
                caze(
                    "ASiC-S null", f -> f.asicDocument = null,
                    "ASiC-S container must not be null"
                ),
                caze(
                    "ASiC-S empty", f -> f.asicDocument = emptyDoc("asic.zip"),
                    "ASiC-S container must not be empty"
                ),
                caze(
                    "business document null", f -> f.businessDocument = null,
                    "Business document must not be null"
                ),
                caze(
                    "business document empty", f -> f.businessDocument = emptyDoc("business.xml"),
                    "Business document must not be empty"
                ),
                caze(
                    "detached signature empty",
                    f -> f.detachedSignature = emptyDoc("signature.p7s"),
                    "detached signature must not be empty"
                ),
                caze(
                    "PDF token null", f -> f.tokenPdf = null,
                    "PDF token must not be null"
                ),
                caze(
                    "PDF token empty", f -> f.tokenPdf = emptyDoc("token.pdf"),
                    "PDF token must not be empty"
                ),
                caze(
                    "XML token null", f -> f.tokenXml = null,
                    "XML token must not be null"
                ),
                caze(
                    "XML token empty", f -> f.tokenXml = emptyDoc("token.xml"),
                    "XML token must not be empty"
                ),

                // token graph
                caze(
                    "token null", f -> f.token = null,
                    "Token must not be null"
                ),
                caze(
                    "token document null",
                    f -> when(f.token.getDocument()).thenReturn(null),
                    "Token document must not be null"
                ),
                caze(
                    "token document filename null",
                    f -> when(f.token.getDocument().getFilename()).thenReturn(null),
                    "Token document filename must not be null"
                ),
                caze(
                    "token document type null",
                    f -> when(f.token.getDocument().getType()).thenReturn(null),
                    "Token document type must not be null"
                ),
                caze(
                    "token document digest method null",
                    f -> when(f.token.getDocument().getDigestMethod()).thenReturn(null),
                    "Token document digest method must not be null"
                ),
                caze(
                    "token document digest value null",
                    f -> when(f.token.getDocument().getDigestValue()).thenReturn(null),
                    "Token document digest value must not be null"
                ),
                caze(
                    "token document digest value empty",
                    f -> when(f.token.getDocument().getDigestValue()).thenReturn(new byte[0]),
                    "Token document digest value must not be null"
                ),
                caze(
                    "token issuer null",
                    f -> when(f.token.getIssuer()).thenReturn(null),
                    "Token issuer must not be null"
                ),

                // validity
                caze(
                    "token validation null",
                    f -> when(f.token.getValidation()).thenReturn(null),
                    "Token validation must not be null"
                ),
                caze(
                    "verification time null",
                    f -> when(f.token.getValidation().getVerificationTime()).thenReturn(null),
                    "verification time must not be null"
                ),
                caze(
                    "verification data null",
                    f -> when(f.token.getValidation().getVerificationData()).thenReturn(null),
                    "verification data must not be null"
                ),
                caze(
                    "technical result null",
                    f -> when(f.token.getValidation().getTechnicalResult()).thenReturn(null),
                    "Token technical validation must not be null"
                ),
                caze(
                    "technical trust level null",
                    f -> when(f.token.getValidation().getTechnicalResult().getTrustLevel())
                        .thenReturn(null),
                    "technical validation trust level must not be null"
                ),
                caze(
                    "legal result null",
                    f -> when(f.token.getValidation().getLegalResult()).thenReturn(null),
                    "Token legal validation must not be null"
                ),
                caze(
                    "legal trust level null",
                    f -> when(f.token.getValidation().getLegalResult().getTrustLevel())
                        .thenReturn(null),
                    "legal validation trust level must not be null"
                ),

                // advanced electronic system
                caze(
                    "advanced system type null",
                    f -> when(f.token.getAdvancedElectronicSystem()).thenReturn(null),
                    "advanced system type must not be null"
                ),

                // AUTHENTICATION_BASED authentication data (default AES type)
                caze(
                    "authentication data null",
                    f -> when(f.token.getValidationVerificationData().getAuthenticationData())
                        .thenReturn(null),
                    "authentication data must not be null when the system is AUTHENTICATION_BASED"
                ),
                caze(
                    "username synonym null",
                    f -> when(f.token.getValidationVerificationData()
                                 .getAuthenticationData().getUsernameSynonym()).thenReturn(null),
                    "username synonym must not be null"
                ),
                caze(
                    "identity provider null",
                    f -> {
                        when(f.token.getValidationVerificationData()
                                    .getAuthenticationData()
                                    .getUsernameSynonym()).thenReturn("user");
                        when(f.token.getValidationVerificationData()
                                    .getAuthenticationData()
                                    .getIdentityProvider()).thenReturn(null);
                    },
                    "identity provider must not be null"
                ),
                caze(
                    "time of authentication null",
                    f -> {
                        when(f.token.getValidationVerificationData()
                                    .getAuthenticationData()
                                    .getUsernameSynonym()).thenReturn("user");
                        when(f.token.getValidationVerificationData()
                                    .getAuthenticationData().getIdentityProvider()).thenReturn(
                            "provider");
                        when(f.token.getValidationVerificationData()
                                    .getAuthenticationData()
                                    .getTimeOfAuthentication()).thenReturn(null);
                    },
                    "time of authentication must not be null"
                ),

                // SIGNATURE_BASED list guards (per-signature field checks pending element type names)
                caze(
                    "signature data null (SIGNATURE_BASED)", f -> {
                        when(f.token.getAdvancedElectronicSystem())
                            .thenReturn(ConnectorTokenAESType.SIGNATURE_BASED);
                        when(f.token.getValidationVerificationData().getSignatureData())
                            .thenReturn(null);
                    },
                    "signature data must not be null when the system is SIGNATURE_BASED"
                ),
                caze(
                    "signature data empty (SIGNATURE_BASED)", f -> {
                        when(f.token.getAdvancedElectronicSystem())
                            .thenReturn(ConnectorTokenAESType.SIGNATURE_BASED);
                        when(f.token.getValidationVerificationData().getSignatureData())
                            .thenReturn(Collections.emptyList());
                    },
                    "signature data must not be null when the system is SIGNATURE_BASED"
                )
            );
        }

        @Test
        void should_reject_null_container() {
            assertThatThrownBy(() -> checker.check(null))
                .isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("invalidCases")
        void should_reject(String name, Consumer<Fixture> mutation, String expectedMessage) {
            var fixture = new Fixture();
            mutation.accept(fixture);

            assertThatThrownBy(() -> checker.check(fixture.build()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(expectedMessage);
        }
    }

    @Nested
    @DisplayName("non-throwing branches")
    class NonThrowing {
        @Test
        void should_skip_aes_checks_when_verification_data_is_absent() {
            var fixture = new Fixture();
            // No validation-verification-data => AES-type checks are short-circuited.
            when(fixture.token.getValidationVerificationData()).thenReturn(null);

            assertThatCode(() -> checker.check(fixture.build()))
                .doesNotThrowAnyException();
        }

        @Test
        void should_only_warn_when_technical_trust_level_is_non_successful_without_comment() {
            var nonSuccessful = Arrays.stream(ConnectorTokenTechnicalTrustLevel.values())
                                      .filter(v -> v != ConnectorTokenTechnicalTrustLevel.SUCCESSFUL)
                                      .findFirst();
            assumeTrue(nonSuccessful.isPresent(), "no non-successful trust level to test");

            var fixture = new Fixture();
            when(fixture.token.getValidation().getTechnicalResult().getTrustLevel())
                .thenReturn(nonSuccessful.get());
            when(fixture.token.getValidation().getTechnicalResult().getComment())
                .thenReturn(null);
            when(fixture.token.getValidationVerificationData()
                              .getAuthenticationData()
                              .getUsernameSynonym())
                .thenReturn("user");
            when(fixture.token.getValidationVerificationData()
                              .getAuthenticationData()
                              .getIdentityProvider())
                .thenReturn("identity-provider");

            assertThatCode(() -> checker.check(fixture.build()))
                .doesNotThrowAnyException();
        }
    }
}

