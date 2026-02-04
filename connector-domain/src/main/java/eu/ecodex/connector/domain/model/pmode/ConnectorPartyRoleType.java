/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.pmode;

import java.io.Serializable;

/**
 * Defines the role type of party in the Connector system.
 *
 * <p>This enumeration classifies the roles a party can assume within the context of a message
 * exchange. It distinguishes whether a party is responsible for initiating communication or
 * responding to it.
 *
 * <p>Enum Constants:
 * <ul>
 *     <li>INITIATOR: Represents a party that initiates the communication.</li>
 *     <li>RESPONDER: Represents a party that responds to the initiated communication.</li>
 * </ul>
 */
public enum ConnectorPartyRoleType implements Serializable {
    INITIATOR,
    RESPONDER
}
