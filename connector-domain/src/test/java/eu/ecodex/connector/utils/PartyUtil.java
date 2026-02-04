package eu.ecodex.connector.utils;

import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class PartyUtil {
    public static ConnectorParty createFromParty() {
        return ConnectorParty.builder()
                             .name("service_blue_ecodex")
                             .identifier("BL")
                             .identifierType("urn:oasis:names:tc:ebcore:partyid-type:ecodex")
                             .role("GW")
                             .roleType(ConnectorPartyRoleType.INITIATOR)
                             .build();
    }

    public static ConnectorParty createToParty() {
        return ConnectorParty.builder()
                             .name("service_red_ecodex")
                             .identifier("RE")
                             .identifierType("urn:oasis:names:tc:ebcore:partyid-type:ecodex")
                             .role("GW")
                             .roleType(ConnectorPartyRoleType.RESPONDER)
                             .build();
    }
}
