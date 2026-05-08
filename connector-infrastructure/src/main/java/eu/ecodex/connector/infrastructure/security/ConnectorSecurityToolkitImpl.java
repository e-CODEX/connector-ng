/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security;

import eu.ecodex.connector.domain.api.ConnectorSecurityToolkit;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.infrastructure.security.container.ConnectorAsicContainerBuilder;
import eu.ecodex.connector.infrastructure.security.container.ConnectorAsicContainerValidator;
import eu.ecodex.connector.infrastructure.security.exception.ConnectorContainerException;
import eu.ecodex.connector.infrastructure.security.model.container.ConnectorContainer;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorSecurityToolkit}.
 */
@Slf4j
@Component
public class ConnectorSecurityToolkitImpl implements ConnectorSecurityToolkit {
    private final ConnectorAsicContainerBuilder asicContainerBuilder;
    private final ConnectorAsicContainerValidator asicContainerValidator;
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorFileStorageProvider fileStorageProvider;

    /**
     * Constructor for the ConnectorSecurityToolkitImpl.
     *
     * @param asicContainerBuilder   the builder used for creating ASiC containers
     * @param asicContainerValidator the validator used for validating ASiC containers
     * @param attachmentRepository   the repository for managing message attachments
     * @param fileStorageProvider    the provider for handling file storage operations
     */
    public ConnectorSecurityToolkitImpl(
            ConnectorAsicContainerBuilder asicContainerBuilder,
            ConnectorAsicContainerValidator asicContainerValidator,
            ConnectorMessageAttachmentRepository attachmentRepository,
            ConnectorFileStorageProvider fileStorageProvider) {
        this.asicContainerBuilder = asicContainerBuilder;
        this.asicContainerValidator = asicContainerValidator;
        this.attachmentRepository = attachmentRepository;
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public void validateMessage(@NonNull ConnectorMessage message) {
        log.debug("Validating message [{}] ASIC-S container", message.identifier());
        this.asicContainerValidator.validate(message);
    }

    @Override
    public ConnectorMessage buildContainer(@NonNull ConnectorMessage message) {
        var messageIdentifier = message.identifier();

        if (messageIdentifier == null) {
            throw new ConnectorContainerException("The message identifier is null");
        }

        try {
            this.validateUniqueAttachmentNames(messageIdentifier);

            var container = this.asicContainerBuilder.createAsicContainer(message);
            this.saveContainer(messageIdentifier, container);
            this.saveXmlToken(messageIdentifier, container);

            return message;
        } catch (ConnectorContainerException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to build ASIC-S container for the message: [{}]", messageIdentifier);
            throw new ConnectorContainerException(
                    "Failed to build ASIC-S container for the message: " + messageIdentifier, e
            );
        }
    }

    private void validateUniqueAttachmentNames(String messageIdentifier) {
        log.debug("Validating unique attachment names for message: [{}]", messageIdentifier);

        var attachments = this.attachmentRepository.findByMessageIdentifierAndTypes(
                messageIdentifier, List.of(ConnectorAttachmentType.ATTACHMENT)
        );

        var seenNames = new HashSet<String>();

        for (var attachment : attachments) {
            var name = attachment.name();

            if (!seenNames.add(name)) {
                throw new ConnectorContainerException(
                        String.format(
                                "Duplicate attachment name [%s] detected for message [%s]",
                                name, messageIdentifier
                        )
                );
            }
        }
    }

    private void saveContainer(String messageIdentifier, ConnectorContainer container)
            throws Exception {
        log.debug("Saving ASIC-S container for message: [{}]", messageIdentifier);

        var attachment = ConnectorMessageAttachment
                .builder()
                .identifier(String.format("message-%s-asics", messageIdentifier))
                .name("container-signed-xades-baseline-b.scs")
                .contentType("application/vnd.etsi.asic-s+zip")
                .size(getDssFileSize(container.asicDocument()))
                .description(String.format("ASIC-S container for message: %s", messageIdentifier))
                .storage(ConnectorAttachmentStorage.S3_BUCKET)
                .type(ConnectorAttachmentType.ASICS)
                .build();

        saveAttachmentFile(
                attachment, messageIdentifier,
                tempPath -> container.asicDocument().save(tempPath.toString()),
                "ASIC-S container"
        );
    }

    private void saveXmlToken(String messageIdentifier, ConnectorContainer container)
            throws Exception {
        log.debug("Saving XML TrustOK Token for message: [{}]", messageIdentifier);

        var attachment = ConnectorMessageAttachment
                .builder()
                .identifier(String.format("message-%s-trustok-token.xml", messageIdentifier))
                .name("Token.xml")
                .contentType("text/xml")
                .size(getDssFileSize(container.tokenXML()))
                .description(String.format("XML TrustOK Token for message: %s", messageIdentifier))
                .storage(ConnectorAttachmentStorage.S3_BUCKET)
                .type(ConnectorAttachmentType.XML_TOKEN)
                .build();

        saveAttachmentFile(
                attachment, messageIdentifier,
                tempPath -> container.tokenXML().save(tempPath.toString()),
                "XML TrustOK Token"
        );
    }

    /**
     * Saves an attachment by: persisting metadata, writing to a temp file, uploading to storage,
     * then deleting the temp file. Temp file is always cleaned up, even on failure.
     */
    private void saveAttachmentFile(
            ConnectorMessageAttachment attachment,
            String messageIdentifier,
            SaveOperation saveOperation,
            String description) throws IOException {
        var savedAttachment = attachmentRepository.save(attachment);
        attachmentRepository.attachToMessage(savedAttachment.identifier(), messageIdentifier);

        // Use system temp dir rather than a hardcoded relative path
        var tempPath = Files.createTempFile("connector-", "-" + savedAttachment.identifier());
        try {
            saveOperation.save(tempPath);
            fileStorageProvider.save(savedAttachment, tempPath);
            Files.delete(tempPath);
        } catch (Exception e) {
            Files.deleteIfExists(tempPath);
            log.error("Failed to save {} for message [{}]", description, messageIdentifier, e);
            throw new ConnectorContainerException(e);
        }
    }

    private int getDssFileSize(DSSDocument dssDocument) {
        try {
            return ((InMemoryDocument) dssDocument).getBytes().length;
        } catch (Exception e) {
            return -1;
        }
    }

    @FunctionalInterface
    private interface SaveOperation {
        void save(Path path) throws Exception;
    }
}
