/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link;

import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerProperties;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class LinkPartnerPropertiesTestFixtures {
    public static ConnectorLinkPartnerProperties createBackendLinkPartnerProperties() {
        return ConnectorLinkPartnerProperties
                .builder()
                .pushAddress("http://localhost:8080/push")
                .encryptionAlias("alice")
                .certificateDn("cn=alice")
                .build();
    }
}
