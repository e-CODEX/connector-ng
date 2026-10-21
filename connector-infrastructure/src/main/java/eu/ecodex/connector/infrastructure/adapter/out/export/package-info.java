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
 * Outbound export adapters: implement the ports of {@link eu.ecodex.connector.application.port.spi}
 * that write domain data to external formats such as XML, CSV or JSON.
 *
 * <p>Format-specific code only; no business rules.
 */
package eu.ecodex.connector.infrastructure.adapter.out.export;
