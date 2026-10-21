/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/**
 * Adapters of the connector: the implementations that connect the core to the outside world.
 *
 * <p>Adapters are split by direction. Inbound adapters (for example REST/SOAP endpoints or JMS
 * listeners) receive requests and call the inbound ports of
 * {@link eu.ecodex.connector.application.port.api}. Outbound adapters (for example persistence,
 * gateway communication or messaging) implement the outbound ports of
 * {@link eu.ecodex.connector.application.port.spi} using concrete technologies.
 *
 * <p>Adapters translate between external representations (HTTP payloads, database rows, gateway
 * messages) and domain types, so that technical details never reach the core. They contain no
 * business rules. Dependencies point inwards: adapters depend on the application and domain layers,
 * never the other way round.
 */
package eu.ecodex.connector.infrastructure.adapter;
