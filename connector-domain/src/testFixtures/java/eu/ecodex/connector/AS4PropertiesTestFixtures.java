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

import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "LineLength"})
public class AS4PropertiesTestFixtures {
    public static ConnectorMessageAS4Properties.ConnectorMessageAS4PropertiesBuilder defaultAS4Properties() {
        return ConnectorMessageAS4Properties
            .builder()
            .conversationIdentifier("e6a173ec-de21-46dc-8a19-63a6cb74915d")
            .ebmsMessageIdentifier(null)
            .originalSender("alice")
            .finalRecipient("bob")
            .fromParty(PartyTestFixtures.createFromParty())
            .toParty(PartyTestFixtures.createToParty())
            .service(ServiceTestFixtures.createService())
            .action(ActionTestFixtures.createAction());
    }

    public static ConnectorMessageAS4Properties createAS4Properties() {
        return defaultAS4Properties().build();
    }

    public static ConnectorMessageAS4Properties createAS4PropertiesWithoutFromParty() {
        return defaultAS4Properties().fromParty(null).build();
    }

    public static ConnectorMessageAS4Properties createAS4PropertiesWithoutToParty() {
        return defaultAS4Properties().toParty(null).build();
    }

    public static ConnectorMessageAS4Properties createAS4PropertiesWithoutService() {
        return defaultAS4Properties().service(null).build();
    }

    public static ConnectorMessageAS4Properties createAS4PropertiesWithoutAction() {
        return defaultAS4Properties().action(null).build();
    }
}
