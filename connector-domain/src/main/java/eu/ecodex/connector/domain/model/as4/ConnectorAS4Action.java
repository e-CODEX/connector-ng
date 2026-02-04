/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.as4;

import lombok.Builder;


/**
 * Represents an AS4 action in the connector domain. This class is used to define and identify
 * specific actions associated with the AS4 communication protocol.
 *
 * <p>An AS4 action typically encapsulates a specific operation or event, identified by its name.
 * This allows for clear differentiation and handling of actions related to messaging and evidence
 * management in AS4-based communication processes.
 *
 * @param name The name of the AS4 action, defining its purpose or type.
 */
@Builder
public record ConnectorAS4Action(
        String name
) {
}
