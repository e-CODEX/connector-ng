/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import java.util.Set;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class ProcessingModeTestFixtures {
    public static ConnectorProcessingMode createWithNoBusinessDomain() {
        return ConnectorProcessingMode
                .builder()
                .description("test processing mode")
                .services(Set.of(ServiceTestFixtures.createService()))
                .actions(Set.of(ActionTestFixtures.createAction()))
                .parties(Set.of(PartyTestFixtures.createFromParty()))
                .homeParty(PartyTestFixtures.createToParty())
                .content(new byte[1])
                .build();
    }

    public static ConnectorProcessingMode createWithBusinessDomain() {
        return ConnectorProcessingMode
                .builder()
                .businessDomain(BusinessDomainTestFixtures.createDefaultBusinessDomain())
                .description("test processing mode")
                .services(Set.of(ServiceTestFixtures.createService()))
                .actions(Set.of(ActionTestFixtures.createAction()))
                .parties(Set.of(PartyTestFixtures.createFromParty()))
                .homeParty(PartyTestFixtures.createToParty())
                .content(new byte[1])
                .build();
    }
}
