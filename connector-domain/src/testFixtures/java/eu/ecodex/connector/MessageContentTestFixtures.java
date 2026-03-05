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

import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class MessageContentTestFixtures {
    public static ConnectorMessageBusinessContent createContent() {
        return ConnectorMessageBusinessContent
                .builder()
                .xmlContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .businessDocument(ConnectorMessageDocumentTestFixtures.createDocument())
                .build();
    }

    public static ConnectorMessageBusinessContent createSaveContent() {
        return createContent()
                .toBuilder()
                .uuid("96a818f9-606a-4ff5-abc0-3ab31fa90ac3")
                .businessDocument(ConnectorMessageDocumentTestFixtures.createSavedDocument())
                .build();
    }
}
