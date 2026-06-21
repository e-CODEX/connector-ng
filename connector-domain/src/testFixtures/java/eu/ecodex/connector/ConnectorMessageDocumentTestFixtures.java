/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.message.content.ConnectorBusinessDocumentAESType;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessDocument;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class ConnectorMessageDocumentTestFixtures {
    public static ConnectorMessageBusinessDocument createSignatureBasedDocument() {
        return ConnectorMessageBusinessDocument
                .builder()
                .attachment(MessageAttachmentTestFixtures.createAttachment())
                .aesType(ConnectorBusinessDocumentAESType.SIGNATURE_BASED)
                .detachedSignature(DetachedSignatureTestFixtures.createDetachedSignature())
                .build();
    }

    public static ConnectorMessageBusinessDocument createAuthenticationBasedDocument() {
        return ConnectorMessageBusinessDocument
                .builder()
                .attachment(MessageAttachmentTestFixtures.createAttachment())
                .aesType(ConnectorBusinessDocumentAESType.AUTHENTICATION_BASED)
                .detachedSignature(DetachedSignatureTestFixtures.createDetachedSignature())
                .build();
    }

    public static ConnectorMessageBusinessDocument createDocumentWithoutSignature() {
        return ConnectorMessageBusinessDocument
                .builder()
                .attachment(MessageAttachmentTestFixtures.createAttachment())
                .build();
    }

    public static ConnectorMessageBusinessDocument createSavedDocument() {
        return createSignatureBasedDocument()
                .toBuilder()
                .uuid("05768e36-dc7d-48e4-a403-1eceeecc1788")
                .build();
    }
}
