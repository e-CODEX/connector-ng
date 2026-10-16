/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest.validator;

import eu.ecodex.connector.infrastructure.adapter.in.web.rest.validator.constraints.NotEmptyFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validator for the {@link NotEmptyFile} constraint.
 *
 * <p>A value is valid if it is not {@code null} and {@link MultipartFile#isEmpty()} returns
 * {@code false}, that is, a file was provided, and it has a size greater than zero. The size is the
 * one reported by the multipart request; the content itself is not inspected.
 *
 * <p>A {@code null} value is reported as invalid, so no separate {@code @NotNull} is needed.
 * Combine the constraint with the multipart size limits
 * ({@code spring.servlet.multipart.max-file-size} and {@code max-request-size}) to bound the size
 * of uploads.
 */
public class NotEmptyFileValidator implements ConstraintValidator<NotEmptyFile, MultipartFile> {
    /**
     * Checks that the given file is present and not empty.
     *
     * @param file    the file to validate; may be {@code null}
     * @param context the context in which the constraint is evaluated
     *
     * @return {@code true} if the file is not {@code null} and not empty, {@code false} otherwise
     */
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return file != null && !file.isEmpty();
    }
}
