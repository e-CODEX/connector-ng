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
 * Represents the type of the connector link in a system that facilitates interactions between
 * different components or services.
 *
 * <p>This enum defines two types of connector links:
 * <ul>
 *     <li>BACKEND: Refers to a link associated with backend operations or services.</li>
 *     <li>GATEWAY: Refers to a link associated with gateway operations or services.</li>
 * </ul>
 *
 * <p>It is used to categorize and handle connections between various system components.
 */
public enum ConnectorLinkType implements Serializable {
    BACKEND, GATEWAY
}
