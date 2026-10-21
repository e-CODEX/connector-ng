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
 * Application services implementing the outbound message use cases of the connector.
 *
 * <p>Services in this package handle messages traveling from a backend towards the gateway. They
 * orchestrate the outbound workflow (preparing the message, triggering its validation, persisting
 * it and handing it over for dispatch) by delegating business rules and validation to the domain
 * model and persistence or gateway submission to outbound ports.
 */
package eu.ecodex.connector.application.service.message.outbound;
