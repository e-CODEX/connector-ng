/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.validator;

import eu.ecodex.connector.infrastructure.inbound.web.rest.validator.constraints.AtLeastOneNotEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

/**
 * Validates that at least one of the specified fields is not empty.
 */
public class AtLeastOneNotEmptyValidator
    implements ConstraintValidator<AtLeastOneNotEmpty, Object> {
    private String[] fields;

    @Override
    public void initialize(AtLeastOneNotEmpty annotation) {
        this.fields = annotation.fields();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        for (var field : fields) {
            try {
                var value = obj.getClass().getDeclaredMethod(field).invoke(obj);
                if (value instanceof String s && StringUtils.hasText(s)) {
                    return true;
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Unknown field: " + field, e);
            }
        }

        // Build dynamic message with actual label names
        var labels = String.join(", ", fields);
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
            "At least one of the following must be provided: " + labels
        ).addConstraintViolation();

        return false;
    }
}
