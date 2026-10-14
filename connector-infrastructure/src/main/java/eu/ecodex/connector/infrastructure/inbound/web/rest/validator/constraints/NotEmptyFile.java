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

import eu.ecodex.connector.infrastructure.inbound.web.rest.validator.NotEmptyFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validates that an annotated {@link MultipartFile} is present and not empty.
 *
 * <p>Unlike {@code @NotNull}, which accepts an empty upload, this constraint rejects both a
 * {@code null} file and a file with zero bytes. It does not check the content of the file nor its
 * type; those checks must be done when the file is processed.
 *
 * <p>Example:
 * <pre>{@code
 * public record ExampleRequest(
 *     @NotEmptyFile(message = "The file must not be empty.")
 *     MultipartFile truststoreFile
 * ) {
 * }
 * }</pre>
 *
 * @see NotEmptyFileValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyFileValidator.class)
public @interface NotEmptyFile {
    /**
     * The message reported when the constraint is violated.
     *
     * @return the violation message, "The file must not be empty" by default
     */
    String message() default "The file must not be empty";

    /**
     * The validation groups the constraint belongs to.
     *
     * @return the groups of the constraint, none by default
     */
    Class<?>[] groups() default {};

    /**
     * The payload that clients can attach to the constraint.
     *
     * @return the payload of the constraint, none by default
     */
    Class<? extends Payload>[] payload() default {};
}
