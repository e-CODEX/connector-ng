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
import eu.ecodex.connector.domain.model.message.content.DetachedSignatureMimeType;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageAS4Properties;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageAction;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageBusinessDocument;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageDetachedSignature;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageParty;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageService;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:MissingJavadocMethod"})
public class ConnectorOutboundMessageTestFixtures {
    public static ConnectorOutboundMessageRequest produceOutboundMessageMetadata(
            List<String> attachments, String businessDocumentIdentifier, boolean attachSignature) {
        return ConnectorOutboundMessageRequest
                .builder()
                .businessDomainIdentifier("default_business_domain")
                .backendMessageIdentifier(UUID.randomUUID() + "@backend_system")
                .attachments(attachments)
                .businessContent(businessDocument(businessDocumentIdentifier, attachSignature))
                .as4Properties(as4Properties())
                .build();
    }

    private static ConnectorOutboundMessageBusinessDocument businessDocument(
            String businessDocumentIdentifier, boolean attachSignature) {
        return ConnectorOutboundMessageBusinessDocument
                .builder()
                .attachmentIdentifier(businessDocumentIdentifier)
                .detachedSignature(attachSignature ? detachedSignature() : null)
                .aesType(attachSignature ? ConnectorBusinessDocumentAESType.SIGNATURE_BASED : null)
                .build();
    }

    private static ConnectorOutboundMessageAS4Properties as4Properties() {
        return ConnectorOutboundMessageAS4Properties
                .builder()
                .conversationIdentifier(UUID.randomUUID().toString())
                .originalSender("alice")
                .finalRecipient("bob")
                .service(
                        ConnectorOutboundMessageService
                                .builder()
                                .name("Connector-TEST")
                                .type("urn:e-codex:services:")
                                .build()
                )
                .action(
                        ConnectorOutboundMessageAction
                                .builder()
                                .name("ConTest_Form")
                                .build()
                )
                .fromParty(outboundMessageParty("BL"))
                .toParty(outboundMessageParty("RE"))
                .build();
    }

    private static ConnectorOutboundMessageParty outboundMessageParty(String identifier) {
        return ConnectorOutboundMessageParty
                .builder()
                .identifier(identifier)
                .identifierType("urn:oasis:names:tc:ebcore:partyid-type:ecodex")
                .role("GW")
                .build();
    }

    private static ConnectorOutboundMessageDetachedSignature detachedSignature() {
        return ConnectorOutboundMessageDetachedSignature
                .builder()
                .mimeType(DetachedSignatureMimeType.XML)
                .name("test_signature.xml")
                .signature(new byte[1])
                .build();
    }
}
