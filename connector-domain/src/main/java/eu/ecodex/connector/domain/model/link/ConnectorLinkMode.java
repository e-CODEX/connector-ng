/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.link;

import java.io.Serializable;

/**
 * Defines the modes of interaction for a connector link within a system that facilitates
 * communication or data transfer between components or services.
 *
 * <ul>
 *     <li>
 *         PUSH: Indicates that the connector actively sends data or requests to the linked
 *         component.
 *     </li>
 *     <li>
 *         PULL: Indicates that the connector actively retrieves data or requests from the linked
 *         component.
 *     </li>
 *     <li>
 *         PASSIVE: Indicates that the connector waits for external triggers to perform operations,
 *         without initiating communication.
 *     </li>
 * </ul>
 *
 * <p>This enum enables configuration and management of different operational modes for connector
 * links.
 */
public enum ConnectorLinkMode implements Serializable {
    PUSH,
    PULL,
    PASSIVE
}
