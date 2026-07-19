/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property.link;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties describing a link partner.
 *
 * <p>A link partner represents a remote system or integration endpoint that exchanges messages
 * with the application. The configuration defines identification, operational modes, and optional
 * settings depending on whether the partner is used by the gateway or backend.
 */
@Getter
@Setter
public class LinkPartnerProperties {
    private String name;
    private String description;
    private boolean enabled;
    private String receiverMode; // TODO: remove
    private String senderMode;
    private LinkPartnerDetailProperties properties; // backend only
}
