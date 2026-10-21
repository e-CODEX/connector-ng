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
 * Outbound evidence adapters of the connector: the implementations through which the core produces
 * and handles evidences using concrete technologies.
 *
 * <p>Classes in this package implement the evidence-related outbound ports of
 * {@link eu.ecodex.connector.application.port.spi}, such as building the technical representation
 * of an evidence (for example its XML content), signing it, or handing it over for storage or
 * transmission. They translate between domain types and the evidence's technical format, so that no
 * format or library details reach the core.
 *
 * <p>Evidence adapters contain no business
 * rules: deciding which evidence applies to a message, and when, belongs to the domain model and
 * the evidence services of the application layer.
 */
package eu.ecodex.connector.infrastructure.adapter.out.evidence;
