/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message;

import java.io.Serializable;

/**
 * Represents the type of direction for a connector message.
 *
 * <p>This enum defines the possible endpoints involved in the message flow:
 * <ul>
 *     <li>GATEWAY: Represents the gateway as a source or target in a message direction.</li>
 *     <li>BACKEND: Represents the backend system as a source or target in a message direction.</li>
 * </ul>
 *
 * <p>The types defined here are used in {@link ConnectorMessageDirection} to identify
 * the origin and destination of a message within the system.
 */
public enum ConnectorMessageDirectionType implements Serializable {
    GATEWAY, BACKEND
}
