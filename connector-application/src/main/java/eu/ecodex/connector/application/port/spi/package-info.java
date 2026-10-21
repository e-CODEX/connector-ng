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
 * Outbound ports of the connector: the capabilities the core requires from the outside world.
 *
 * <p>Each interface in this package is defined by the needs of the application and domain layers
 * and expressed in their language, so that use cases can rely on a capability (for example,
 * persistence, gateway submission or evidence handling) without knowing how it is provided.
 * Outbound adapters implement these interfaces and are plugged in at runtime.
 *
 * <p>Interfaces in this package should:
 *
 * <ul>
 *   <li>describe a capability required by the core, not the technology that fulfils it;
 *   <li>use domain types in their signatures, never framework, transport or persistence types;
 *   <li>stay free of implementation logic.
 * </ul>
 *
 * <p>This is the counterpart of the inbound ports in
 * {@link eu.ecodex.connector.application.port.api}.
 * Dependencies point inwards: adapters depend on these ports, never the other way round.
 */
package eu.ecodex.connector.application.port.spi;
