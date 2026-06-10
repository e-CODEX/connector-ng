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

import eu.ecodex.connector.domain.transition.DomibusConnectorActionType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDocumentAESType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorPartyType;
import eu.ecodex.connector.domain.transition.DomibusConnectorServiceType;
import eu.ecodex.connector.infrastructure.inbound.web.soap.helper.AttachmentHelpers;
import jakarta.activation.DataHandler;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

@SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:MissingJavadocType"})
public class SoapMessageSubmitTestFixtures {
    public static DomibusConnectorMessageType createBackendToConnectorMessage() {
        var message = new DomibusConnectorMessageType();
        message.setMessageDetails(createMessageDetails());
        message.getMessageAttachments().add(createAttachment());
        message.setMessageContent(createMessageContent());
        return message;
    }

    public static DomibusConnectorMessageType createBackendToConnectorMessageWithoutAttachment() {
        var message = new DomibusConnectorMessageType();
        message.setMessageDetails(createMessageDetails());
        message.setMessageContent(createMessageContent());
        message.getMessageAttachments();
        return message;
    }

    public static byte[] resourceBytes(String filename) {
        try (var is = DomibusConnectorMessageAttachmentType.class
                .getClassLoader()
                .getResourceAsStream(filename)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + filename);
            }
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] expectedBusinessXmlBytes() {
        try {
            return Files.readAllBytes(AttachmentHelpers.sourceToTempFile(xmlSource()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DomibusConnectorMessageDetailsType createMessageDetails() {
        var details = new DomibusConnectorMessageDetailsType();
        details.setBackendMessageId("0093ac10-447c-4946-89a8-dcd9e7a2f4ab");
        details.setConversationId("990a961e-2756-4fb0-97cd-533c93b20054");
        details.setOriginalSender("alice");
        details.setFinalRecipient("bob");
        details.setService(connectorService());
        details.setAction(connectorAction());
        details.setFromParty(connectorFromParty());
        details.setToParty(connectorToParty());

        return details;
    }

    private static DomibusConnectorMessageContentType createMessageContent() {
        var content = new DomibusConnectorMessageContentType();
        content.setXmlContent(xmlSource());
        content.setDocument(connectorMessageDocument());

        return content;
    }

    private static DomibusConnectorMessageAttachmentType createAttachment() {
        var attachment = new DomibusConnectorMessageAttachmentType();
        attachment.setIdentifier("ee5c1011-98ea-4d68-b8f7-88147c5ae69c");
        attachment.setMimeType("text/plain");
        attachment.setName("fake_file.txt");
        attachment.setDescription("proof of theft");
        attachment.setAttachment(dataHandler("raw/fake_file.txt"));

        return attachment;
    }

    private static DomibusConnectorMessageDocumentType connectorMessageDocument() {
        var document = new DomibusConnectorMessageDocumentType();
        document.setDocumentName("Form_A.pdf");
        document.setDocument(dataHandler("raw/fake_file.pdf"));
        document.setAesType(DomibusConnectorDocumentAESType.SIGNATURE_BASED);
        document.setDetachedSignature(connectorDetachedSignature());

        return document;
    }

    private static DomibusConnectorDetachedSignatureType connectorDetachedSignature() {
        var signature = new DomibusConnectorDetachedSignatureType();
        signature.setDetachedSignatureName("signature.xml");
        signature.setDetachedSignature(new byte[]{1, 2, 3, 4});
        signature.setMimeType(DomibusConnectorDetachedSignatureMimeType.XML);

        return signature;
    }

    private static DomibusConnectorServiceType connectorService() {
        var service = new DomibusConnectorServiceType();
        service.setService("Connector-TEST");
        service.setServiceType("urn:e-codex:services:");

        return service;
    }

    private static DomibusConnectorActionType connectorAction() {
        var action = new DomibusConnectorActionType();
        action.setAction("ConTest_Form");

        return action;
    }

    private static DomibusConnectorPartyType connectorFromParty() {
        var party = new DomibusConnectorPartyType();
        party.setPartyId("BL");
        party.setPartyIdType("urn:oasis:names:tc:ebcore:partyid-type:ecodex");
        party.setRole("GW");

        return party;
    }

    private static DomibusConnectorPartyType connectorToParty() {
        var party = new DomibusConnectorPartyType();
        party.setPartyId("RE");
        party.setPartyIdType("urn:oasis:names:tc:ebcore:partyid-type:ecodex");
        party.setRole("GW");

        return party;
    }

    private static DataHandler dataHandler(String filename) {
        var is = DomibusConnectorMessageAttachmentType.class
                .getClassLoader()
                .getResourceAsStream(filename);

        if (is == null) {
            throw new IllegalArgumentException(
                    "Resource not found: " + filename);
        }

        byte[] documentBytes;
        try {
            documentBytes = is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var dataSource = new ByteArrayDataSource(documentBytes, "text/plain");

        return new DataHandler(dataSource);
    }

    private static Source xmlSource() {
        String xml =
                "<invoice>"
                + "   <id>123</id>"
                + "   <amount>100.00</amount>"
                + "</invoice>";

        return new StreamSource(new StringReader(xml));
    }
}
