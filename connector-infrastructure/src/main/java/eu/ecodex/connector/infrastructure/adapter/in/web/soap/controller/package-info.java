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
 * SOAP endpoints of the connector.
 *
 * <p>Each endpoint exposes a web service operation of the connector over SOAP. An endpoint
 * receives the SOAP request, translates its payload into domain types, calls the inbound ports of
 * {@link eu.ecodex.connector.application.port.api}, and builds the SOAP response from the result,
 * or a SOAP fault when the core raises an exception.
 *
 * <p>Endpoints here contain no business rules: they depend on the ports only, never on the
 * application services directly, and keep protocol concerns (SOAP envelopes, faults, WSDL-bound
 * types, namespaces) out of the core.
 */
package eu.ecodex.connector.infrastructure.adapter.in.web.soap.controller;
