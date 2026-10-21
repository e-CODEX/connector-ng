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
 * Helper classes of the connector's SOAP adapters.
 *
 * <p>Classes in this package support the SOAP endpoints of
 * {@link eu.ecodex.connector.infrastructure.adapter.in.web.soap.controller} with
 * protocol-level tasks, such as building and parsing SOAP messages, handling attachments and
 * mapping between WSDL-bound types and domain types. They keep this plumbing out of the endpoints,
 * which stay focused on calling the inbound ports of
 * {@link eu.ecodex.connector.application.port.api}.
 *
 * <p>Helpers are internal to the SOAP adapter: they are stateless, contain no business rules and
 * must not be used by other layers.
 */
package eu.ecodex.connector.infrastructure.adapter.in.web.soap.helper;
