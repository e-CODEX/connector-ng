/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.container.checks.businesscontent;

import eu.ecodex.connector.infrastructure.outbound.security.container.ConnectorContainerFileDefinitions;
import eu.ecodex.connector.infrastructure.outbound.security.container.checks.ConnectorMessageContainerChecker;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainer;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Checker for the business content names.
 */
@Slf4j
@Order(3)
@Component("ConnectorContainerBusinessContentNamesChecker")
public class ConnectorContainerBusinessContentNamesChecker implements
    ConnectorMessageContainerChecker {
    @Override
    public void check(@NonNull ConnectorContainer container) {
        var businessContent = container.businessContent();

        if (businessContent == null) {
            throw new IllegalStateException("The business content must not be null");
        }

        var seenNames = new NameTracker();

        checkNameCompliance(businessContent.getDocument(), "business-content", seenNames);

        var signature = businessContent.getDetachedSignature();

        if (signature != null) {
            checkNameCompliance(signature, "detached-signature", seenNames);
        }

        var attachments = businessContent.getAttachments();

        if (attachments != null) {
            for (int i = 0; i < attachments.size(); i++) {
                checkNameCompliance(attachments.get(i), "attachment#" + i, seenNames);
            }
        }
    }

    private void checkNameCompliance(
        DSSDocument document,
        String documentReference,
        NameTracker seenNames) {
        if (document == null) {
            throw new IllegalStateException(
                "The document [%s] must not be null".formatted(documentReference));
        }
        if (DSSUtils.isEmpty(document)) {
            throw new IllegalStateException(
                "The document [%s] must not be empty".formatted(documentReference));
        }

        var name = document.getName();
        if (!StringUtils.hasText(name)) {
            throw new IllegalStateException(
                "The document [%s] must have a name".formatted(documentReference));
        }
        if (ConnectorContainerFileDefinitions.TOKEN_PDF_REF.equalsIgnoreCase(name)) {
            throw new IllegalStateException(
                "The document [%s] has a name that is reserved".formatted(documentReference));
        }

        if (!seenNames.addExact(name)) {
            log.warn(
                "The document [{}] has the name [{}] that has already been used before; the "
                    + "previous document will be overwritten in the final asic-container.",
                documentReference, name
            );
        } else if (!seenNames.addNormalized(name)) {
            log.warn(
                "The document [{}] has the name [{}] collides with a previously used name that "
                    + "differs only in case, which is likely to cause confusion.",
                documentReference, name
            );
        }
    }

    private static final class NameTracker {
        private final Set<String> exact = new HashSet<>();
        private final Set<String> normalized = new HashSet<>();

        /**
         * Adds the provided name to the set of exact names.
         *
         * @param name the name to be added to the exact name set
         *
         * @return true if the name was added successfully; false if it was already present in the
         *     set
         */
        boolean addExact(String name) {
            return exact.add(name);
        }

        /**
         * Adds the provided name to the set of normalized names in uppercase format. The
         * normalization uses the default locale's ROOT setting for case conversion.
         *
         * @param name the name to be added to the normalized set
         *
         * @return true if the name was successfully added to the set; false if it was already
         *     present in the set
         */
        boolean addNormalized(String name) {
            return normalized.add(name.toUpperCase(Locale.ROOT));
        }
    }
}
