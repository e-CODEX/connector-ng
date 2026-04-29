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
                .build();
    }
}
