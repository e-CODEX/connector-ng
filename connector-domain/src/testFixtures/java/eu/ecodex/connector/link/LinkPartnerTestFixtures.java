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

import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.domain.model.link.ConnectorLinkMode;
import eu.ecodex.connector.domain.model.link.ConnectorLinkType;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class LinkPartnerTestFixtures {
    public static ConnectorLinkPartner createLinkPartner() {
        var builder = createAbstractLinkPartner();
        return builder.build();
    }

    public static ConnectorLinkPartner createDefaultGatewayLinkPartner() {
        var builder = createAbstractLinkPartner();
        builder.type(ConnectorLinkType.GATEWAY);
        builder.name(ConnectorLinkPartnerName.builder().name("default_gateway").build());
        return builder.build();
    }

    public static ConnectorLinkPartner createDefaultBackendLinkPartner() {
        var builder = createAbstractLinkPartner();
        builder.type(ConnectorLinkType.BACKEND);
        builder.name(ConnectorLinkPartnerName.builder().name("default_backend").build());
        return builder.build();
    }

    public static ConnectorLinkPartner createAliceBackendLinkPartner() {
        var builder = createAbstractLinkPartner();
        builder.type(ConnectorLinkType.BACKEND);
        builder.name(ConnectorLinkPartnerName.builder().name("backend_alice").build());
        builder.encryptionAlias("alice");
        builder.certificateDn("cn=alice");
        return builder.build();
    }

    private static ConnectorLinkPartner.ConnectorLinkPartnerBuilder createAbstractLinkPartner() {
        return ConnectorLinkPartner
                .builder()
                .name(
                        ConnectorLinkPartnerName.builder().name("default_gateway").build()
                )
                .description("linkPartnerDescription")
                .enabled(true)
                .senderMode(ConnectorLinkMode.PUSH)
                .type(ConnectorLinkType.BACKEND)
                .source(ConnectorConfigurationSource.IMPLEMENTATION);
    }
}
