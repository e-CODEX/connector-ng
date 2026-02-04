/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.utils;

import eu.ecodex.connector.domain.model.message.ConnectorMessageAttachment;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class MessageAttachmentUtil {
    public static ConnectorMessageAttachment createAttachment() {
        return ConnectorMessageAttachment.builder()
                                         .name("test_attachment")
                                         .description("test attachment description")
                                         .mimeType("application/pdf")
                                         .build();
    }

    public static ConnectorMessageAttachment createSavedAttachment() {
        return createAttachment()
                .toBuilder()
                .uuid("c12f879b-3c9a-4d26-b36c-b6d67a84f0ed")
                .build();
    }
}
