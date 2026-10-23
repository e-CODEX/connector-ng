/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message;

import eu.ecodex.connector.domain.model.ConnectorPublishable;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.List;

/**
 * Represents a message exchanged within the connector system.
 *
 * <p>This sealed interface defines the common properties and behaviors for all connector messages,
 * including business messages and evidence messages. It extends the {@link ConnectorPublishable}
 * interface, which marks messages that can be published to the message broker.
 *
 * <p>The implementing classes, {@link ConnectorBusinessMessage} and
 * {@link ConnectorEvidenceMessage}, provide contextual implementations that add specific fields and
 * methods necessary for handling different message types.
 *
 * <p>The messages are characterised by various identifiers for tracking and routing purposes,
 * along with metadata detailing the involved systems and their roles within the transaction.
 */
public sealed interface ConnectorMessage extends ConnectorPublishable
    permits ConnectorBusinessMessage, ConnectorEvidenceMessage {
    ConnectorBusinessDomainIdentifier businessDomainIdentifier();

    String identifier();

    String backendMessageIdentifier();

    String backendName();

    String gatewayName();

    ConnectorMessageAS4Properties as4Properties();

    ConnectorMessageDirection direction();

    List<ConnectorMessageEvidence> transportedEvidences();
}
