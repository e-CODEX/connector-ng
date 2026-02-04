/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the annotated class represents a domain service within the context of a
 * domain-driven design (DDD) architecture.
 *
 * <p>A domain service is a stateless service that contains domain-specific business logic which
 * does not naturally belong to an individual entity or value object. It operates on one or more
 * domain objects to enforce business rules and processes. This annotation serves as a marker to
 * emphasize the role of the class within the domain layer.
 *
 * <p>This annotation is typically used for classes that implement business functionality central
 * to the domain without acting as a representation of a single domain entity.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainService {
}
