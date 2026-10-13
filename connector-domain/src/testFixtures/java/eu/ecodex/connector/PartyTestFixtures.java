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
