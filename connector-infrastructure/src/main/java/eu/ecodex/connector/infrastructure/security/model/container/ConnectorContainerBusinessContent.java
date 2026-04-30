/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.model.container;

import eu.ecodex.connector.infrastructure.security.container.ConnectorContainerFileDefinitions;
import eu.europa.esig.dss.model.DSSDocument;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Container for business content used in a connector context.
 *
 * <p>This class encapsulates a primary document, an optional detached signature, and a collection
 * of additional attachments.
 *
 * <p>It also provides validation logic to ensure that the main document meets minimum requirements
 * such as:
 * <ul>
 *     <li>Presence of the document</li>
 *     <li>Non-empty content</li>
 *     <li>A valid, non-blank name</li>
 *     <li>Name not matching restricted tokens</li>
 * </ul>
 * </p>
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class ConnectorContainerBusinessContent {
    private DSSDocument document;
    private DSSDocument detachedSignature;
    private List<DSSDocument> attachments = new LinkedList<>();

    /**
     * Adds an attachment to the container.
     *
     * @param attachment the attachment to add; must not be {@code null}
     */
    public void addAttachment(@NonNull final DSSDocument attachment) {
        attachments.add(attachment);
    }

    /**
     * Validates the state of this container.
     *
     * <p>
     * A container is considered valid if:
     * <ul>
     *     <li>The main document is not {@code null}
     *     <li>The document contains readable data
     *     <li>The document name is not blank
     *     <li>The document name does not match restricted identifiers (e.g. {@code TOKEN_PDF_REF})
     * </ul>
     * </p>
     *
     * @return {@code true} if the container is valid; {@code false} otherwise
     */
    public boolean isValid() {
        if (document == null) {
            return false;
        }

        // TODO see if duplication check should be reproduce
        // NICE-TO-HAVE: check for other sensitive names
        // for example: .exe .mp3 .avi .mpg ...
        // e.g. via a (properties) file in the classpath

        var documentName = document.getName();

        return hasData(document)
               && StringUtils.hasText(documentName)
               && !ConnectorContainerFileDefinitions.TOKEN_PDF_REF.equalsIgnoreCase(documentName);
    }

    private boolean hasData(@NonNull final DSSDocument document) {
        try (var in = document.openStream()) {
            return in.read() != -1;
        } catch (final Exception e) {
            log.debug("Failed to open stream for document [{}]", document.getName(), e);
            return false;
        }
    }
}
