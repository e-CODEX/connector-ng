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
 * Input validators of the REST API: format and consistency checks on incoming requests.
 *
 * <p>Complement Bean Validation annotations on the request types; contain no business rules,
 * which belong to the domain model.
 */
package eu.ecodex.connector.infrastructure.adapter.in.web.rest.validator;
