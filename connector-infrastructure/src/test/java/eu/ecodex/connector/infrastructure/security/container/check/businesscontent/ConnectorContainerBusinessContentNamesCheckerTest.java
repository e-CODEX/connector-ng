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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.infrastructure.outbound.security.container.ConnectorContainerFileDefinitions;
import eu.ecodex.connector.infrastructure.outbound.security.container.checks.businesscontent.ConnectorContainerBusinessContentNamesChecker;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainer;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainerBusinessContent;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("DataFlowIssue")
@DisplayName("ConnectorContainerBusinessContentNamesChecker")
class ConnectorContainerBusinessContentNamesCheckerTest {
    private final ConnectorContainerBusinessContentNamesChecker checker =
        new ConnectorContainerBusinessContentNamesChecker();

    private static DSSDocument doc(String name) {
        return new InMemoryDocument(new byte[]{1, 2, 3}, name);
    }

    private static DSSDocument emptyDoc(String name) {
        return new InMemoryDocument(new byte[0], name);
    }

    private static ConnectorContainerBusinessContent withDocument(DSSDocument document) {
        var content = new ConnectorContainerBusinessContent();
        content.setDocument(document);
        return content;
    }

    static Stream<Arguments> invalidCases() {
        var reserved = ConnectorContainerFileDefinitions.TOKEN_PDF_REF;
        return Stream.of(
            // business document
            caze(
                "business document null",
                ConnectorContainerBusinessContent::new,
                "[business-content]", "must not be null"
            ),
            caze(
                "business document empty",
                () -> withDocument(emptyDoc("business-content.xml")),
                "[business-content]", "must not be empty"
            ),
            caze(
                "business document blank name",
                () -> withDocument(doc("   ")),
                "[business-content]", "must have a name"
            ),
            caze(
                "business document reserved name",
                () -> withDocument(doc(reserved)),
                "[business-content]", "reserved"
            ),

            // detached signature (optional slot)
            caze(
                "detached signature empty", () -> {
                    var content = withDocument(doc("business-content.xml"));
                    content.setDetachedSignature(emptyDoc("signature.p7s"));
                    return content;
                },
                "[detached-signature]", "must not be empty"
            ),
            caze(
                "detached signature reserved name", () -> {
                    var content = withDocument(doc("business-content.xml"));
                    content.setDetachedSignature(doc(reserved));
                    return content;
                },
                "[detached-signature]", "reserved"
            ),

            // attachments (indexed slot)
            caze(
                "attachment empty", () -> {
                    var content = withDocument(doc("business-content.xml"));
                    content.addAttachment(emptyDoc("attachment.xml"));
                    return content;
                },
                "[attachment#0]", "must not be empty"
            ),
            caze(
                "attachment reserved name", () -> {
                    var content = withDocument(doc("business-content.xml"));
                    content.addAttachment(doc(reserved));
                    return content;
                },
                "[attachment#0]", "reserved"
            ),
            caze(
                "second attachment reports its index", () -> {
                    var content = withDocument(doc("business-content.xml"));
                    content.addAttachment(doc("first-attachment.xml"));
                    content.addAttachment(doc(reserved));
                    return content;
                },
                "[attachment#1]", "reserved"
            )
        );
    }

    private static Arguments caze(
        String name,
        Supplier<ConnectorContainerBusinessContent> content,
        String reference,
        String reason) {
        return Arguments.of(name, content, reference, reason);
    }

    @Test
    void should_accept_a_business_content_with_a_single_valid_document() {
        var container = mock(ConnectorContainer.class);
        when(container.businessContent()).thenReturn(withDocument(doc("business-content.xml")));
        assertThatCode(() -> checker.check(container))
            .doesNotThrowAnyException();
    }

    @Test
    void should_reject_null_business_content() {
        assertThatThrownBy(() -> checker.check(null))
            .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCases")
    void should_reject(
        String name,
        Supplier<ConnectorContainerBusinessContent> content,
        String reference,
        String reason) {
        var container = mock(ConnectorContainer.class);
        when(container.businessContent()).thenReturn(content.get());
        assertThatThrownBy(() -> checker.check(container))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(reference)
            .hasMessageContaining(reason);
    }
}
