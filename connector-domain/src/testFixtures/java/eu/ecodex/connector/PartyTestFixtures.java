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

import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingModeParty;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class PartyTestFixtures {
    public static ConnectorProcessingModeParty createFromParty() {
        return ConnectorProcessingModeParty
            .builder()
            .name("service_blue_ecodex")
            .identifier("BL")
            .identifierType("urn:oasis:names:tc:ebcore:partyid-type:ecodex")
            .role("GW")
            .roleType(ConnectorPartyRoleType.INITIATOR)
            .build();
    }

    public static ConnectorProcessingModeParty createStagingFromParty() {
        return createFromParty().toBuilder()
                                .name(null)
                                .identifier("BL")
                                .identifierType("urn:oasis:names:tc:ebcore:partyid-type:ecodex")
                                .role("GW")
                                .build();
    }

    public static ConnectorProcessingModeParty createToParty() {
        return ConnectorProcessingModeParty.builder()
                                           .name("service_red_ecodex")
                                           .identifier("RE")
                                           .identifierType(
                                               "urn:oasis:names:tc:ebcore:partyid-type:ecodex")
                                           .role("GW")
                                           .roleType(ConnectorPartyRoleType.RESPONDER)
                                           .build();
    }

    public static ConnectorProcessingModeParty createToStagingParty() {
        return createToParty().toBuilder()
                              .name(null)
                              .identifier("RE")
                              .identifierType("urn:oasis:names:tc:ebcore:partyid-type:ecodex")
                              .role("GW")
                              .build();
    }
}
