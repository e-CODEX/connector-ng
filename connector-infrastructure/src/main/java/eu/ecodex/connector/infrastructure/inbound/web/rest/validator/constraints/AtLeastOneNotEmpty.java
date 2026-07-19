/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.validator.constraints;

import eu.ecodex.connector.infrastructure.inbound.web.rest.validator.AtLeastOneNotEmptyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify that at least one of the defined fields in a target object must contain a
 * non-empty value. This constraint is commonly used to enforce validation where a certain logical
 * group of fields must not all be empty.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneNotEmptyValidator.class)
@Documented
public @interface AtLeastOneNotEmpty {
    /**
     * Specifies the default message to be used when the validation constraint is violated. This
     * message is displayed when none of the specified fields contain non-empty values.
     *
     * @return the default validation error message.
     */
    String message() default "At least one identifier must be provided";

    /**
     * Specifies the group or groups the constraint belongs to. This can be used to group
     * constraints for different validation phases or purposes.
     *
     * @return an array of classes representing the validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Specifies custom payload objects that can be associated with the constraint. Payloads are
     * typically used to carry metadata information consumed by a validation client (e.g., severity
     * level or error codes).
     *
     * @return an array of classes that inherit from Payload to provide additional details about the
     *     constraint violation.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Specifies the names of fields in the target object that need to be checked for non-empty
     * values to satisfy the validation constraint.
     *
     * @return an array of field names that must be checked for non-emptiness.
     */
    String[] fields();
}
