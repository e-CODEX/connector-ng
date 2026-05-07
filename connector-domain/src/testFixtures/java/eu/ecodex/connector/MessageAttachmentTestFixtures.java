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

import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class MessageAttachmentTestFixtures {
    public static ConnectorMessageAttachment createAttachment() {
        var name = "test_attachment";
        var identifier = String.format("%s_%s", "c12f879b-3c9a-4d26-b36c-b6d67a84f0ed", name);
        return ConnectorMessageAttachment
                .builder()
                .identifier(identifier)
                .name(name + ".txt")
                .size(100L)
                .description("test attachment description")
                .contentType("text/plain")
                .storage(ConnectorAttachmentStorage.S3_BUCKET)
                .type(ConnectorAttachmentType.ATTACHMENT)
                .build();
    }

    public static ConnectorMessageAttachment createBusinessContentAttachment() {
        return createAttachment()
                .toBuilder()
                .identifier("104ebc70-abd5-45da-8c74-940d687501b3_messageContent")
                .type(ConnectorAttachmentType.BUSINESS_CONTENT)
                .contentType("text/xml")
                .name("businessContent.xml")
                .build();
    }

    public static ConnectorMessageAttachment createEvidenceAttachment() {
        return createAttachment()
                .toBuilder()
                .identifier("c3e18064-e0da-4170-9733-1e7e2768e0bb_SUBMISSION_ACCEPTANCE")
                .type(ConnectorAttachmentType.EVIDENCE_XML)
                .contentType("text/xml")
                .name("evidence.xml")
                .build();
    }

    public static ConnectorMessageAttachment createAsicsAttachment() {
        return createAttachment()
                .toBuilder()
                .identifier("d9368fda-92f2-498f-95bf-1ca6f4985b85_ASIC-S")
                .type(ConnectorAttachmentType.ASICS)
                .contentType("application/vnd.etsi.asic-s+zip")
                .name("ASICS.zip")
                .build();
    }

    public static ConnectorMessageAttachment createXmlTokenAttachment() {
        return createAttachment()
                .toBuilder()
                .identifier("0f942a85-1e4d-4e36-9432-1625a582b20c_tokenXML")
                .type(ConnectorAttachmentType.XML_TOKEN)
                .contentType("text/xml")
                .name("tokenXML.zip")
                .build();
    }
}
