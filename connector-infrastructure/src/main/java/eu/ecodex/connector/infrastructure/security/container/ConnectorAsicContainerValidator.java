/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.container;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.infrastructure.security.exception.ConnectorContainerException;
import eu.ecodex.connector.infrastructure.security.model.container.ConnectorContainer;
import eu.ecodex.connector.infrastructure.security.model.container.ConnectorContainerBusinessContent;
import eu.ecodex.connector.infrastructure.security.util.DSSDocumentUtil;
import eu.ecodex.connector.infrastructure.security.util.XMLStreamUtil;
import eu.ecodex.connector.infrastructure.security.util.ZipStreamUtil;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * The ConnectorAsicContainerValidator is responsible for validating ASIC-S containers in incoming
 * messages. It ensures that the provided message contains valid ASIC-S and XML trust OK token
 * attachments and processes the container contents. This class interacts with a file storage
 * provider to retrieve attachment contents, validates the structure and type of the container, and
 * persists its contents as necessary.
 */
@Slf4j
@Component
public class ConnectorAsicContainerValidator {
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorFileStorageProvider fileStorageProvider;

    public ConnectorAsicContainerValidator(
            ConnectorMessageAttachmentRepository attachmentRepository,
            ConnectorFileStorageProvider fileStorageProvider) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorageProvider = fileStorageProvider;
    }

    /**
     * Validates the presence and integrity of required attachments in the given connector message,
     * specifically the ASIC-S container and XML trust token. Ensures the attachments are
     * retrievable from storage, processes their contents, and persists the extracted content.
     *
     * @param message the connector message containing attachments to be validated
     *
     * @throws IllegalArgumentException    if the message lacks attachments or required attachment
     *                                     types
     * @throws ConnectorContainerException if an error occurs while processing the attachments, such
     *                                     as I/O issues or XML parsing errors
     */
    public void validate(@NonNull ConnectorMessage message) {
        log.info("Validating ASIC-S container for message {}", message.identifier());
        var attachments = message.attachments();

        if (attachments == null || attachments.isEmpty()) {
            throw new IllegalArgumentException("Message must have at least one attachment");
        }

        var asicAttachment = findAttachmentByType(attachments, ConnectorAttachmentType.ASICS)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Message must have an ASIC-S attachment"));

        var xmlTokenAttachment = findAttachmentByType(
                attachments,
                ConnectorAttachmentType.XML_TOKEN
        )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Message must have an XML trust OK token attachment"));

        try {
            var asicsBytes = fileStorageProvider.findByIdentifier(asicAttachment.identifier());
            var xmlTokenBytes = fileStorageProvider.findByIdentifier(
                    xmlTokenAttachment.identifier());
            var container = buildContainer(asicsBytes, xmlTokenBytes);
            persistAsicsContent(container, message);
        } catch (IOException | JAXBException e) {
            // TODO: remove ASIC-S from S3 bucket on failure
            throw new ConnectorContainerException("I/O error loading ASIC-S attachment", e);
        }
    }

    private ConnectorContainer buildContainer(byte[] asicsBytes, byte[] xmlTokenBytes)
            throws IOException, JAXBException {
        var asicsDocument = new InMemoryDocument(
                asicsBytes,
                ConnectorContainerFileDefinitions.SIGNED_CONTENT_ASIC_REF
        );

        var xmlTokenDocument = new InMemoryDocument(
                xmlTokenBytes,
                ConnectorContainerFileDefinitions.TOKEN_XML_REF
        );

        if (!DSSDocumentUtil.hasData(asicsDocument)) {
            throw new ConnectorContainerException("ASiC-S container is empty");
        }

        if (!DSSDocumentUtil.hasData(xmlTokenDocument)) {
            throw new ConnectorContainerException("XML trust OK token is empty");
        }

        if (!ZipStreamUtil.isZipFile(asicsDocument)) {
            throw new ConnectorContainerException("ASiC-S container is not a ZIP file");
        }

        if (!XMLStreamUtil.isXmlFile(xmlTokenDocument)) {
            throw new ConnectorContainerException("XML trust OK token is not an XML file");
        }

        var token = XMLStreamUtil.decodeXMLStream(xmlTokenDocument.openStream());
        var businessDocumentName = token.getDocument().getFilename();
        var detachedSignatureName = token.getDocument().getSignatureFilename();

        var asicsContents = ZipStreamUtil.extract(asicsDocument);
        // TODO review
        var businessContent = new ConnectorContainerBusinessContent();
        DSSDocument pdfTrustOKToken = null;

        for (var asicsContent : asicsContents) {
            log.debug("ASiC-S entry: {}", asicsContent.getName());
            if (!ConnectorContainerFileDefinitions.SIGNED_CONTENT_REF
                    .equalsIgnoreCase(asicsContent.getName())) {
                continue;
            }
            pdfTrustOKToken = classifyZipEntries(
                    ZipStreamUtil.extract(asicsContent),
                    businessContent,
                    businessDocumentName,
                    detachedSignatureName
            );
        }

        return new ConnectorContainer(
                businessContent,
                token,
                xmlTokenDocument,
                pdfTrustOKToken,
                asicsDocument
        );
    }

    /**
     * Routes each entry in the signed content ZIP into its destination slot. Returns the PDF token
     * document, or null if not present (caller must validate).
     */
    private DSSDocument classifyZipEntries(
            List<DSSDocument> entries,
            ConnectorContainerBusinessContent businessContent,
            String businessDocumentName,
            String detachedSignatureName) {
        DSSDocument pdfToken = null;

        for (var entry : entries) {
            var name = entry.getName();
            log.debug("Signed content entry: {}", name);

            if (ConnectorContainerFileDefinitions.TOKEN_PDF_REF.equalsIgnoreCase(name)) {
                pdfToken = entry;
            } else if (StringUtils.hasText(businessDocumentName)
                       && businessDocumentName.equalsIgnoreCase(name)) {
                businessContent.setDocument(entry);
            } else if (StringUtils.hasText(detachedSignatureName)
                       && detachedSignatureName.equalsIgnoreCase(name)) {
                businessContent.setDetachedSignature(entry);
            } else {
                businessContent.addAttachment(entry);
            }
        }

        return pdfToken;
    }

    private Optional<ConnectorMessageAttachment> findAttachmentByType(
            List<ConnectorMessageAttachment> attachments,
            ConnectorAttachmentType type) {
        return attachments.stream()
                          .filter(a -> type.equals(a.type()))
                          .findFirst();
    }

    private void persistAsicsContent(ConnectorContainer container, ConnectorMessage message)
            throws IOException {

        persistAttachment(
                newAttachmentMetadata(
                        ConnectorContainerFileDefinitions.TOKEN_PDF.name(),
                        "application/pdf",
                        ConnectorAttachmentType.PDF_TOKEN,
                        documentSize(container.tokenPDF())
                ),
                container.tokenPDF().openStream(),
                message.identifier()
        );

        var businessDocument = container.businessContent().getDocument();
        if (businessDocument != null) {
            persistAttachment(
                    newAttachmentMetadata(
                            container.token().getDocument().getFilename(),
                            businessDocument.getMimeType().getMimeTypeString(),
                            ConnectorAttachmentType.BUSINESS_DOCUMENT,
                            documentSize(businessDocument)
                    ),
                    businessDocument.openStream(),
                    message.identifier()
            );
        }

        var detachedSignature = container.businessContent().getDetachedSignature();
        if (detachedSignature != null) {
            persistAttachment(
                    newAttachmentMetadata(
                            detachedSignature.getName(),
                            // TODO: use BINARY type if getMimeType() returns null
                            detachedSignature.getMimeType().getMimeTypeString(),
                            ConnectorAttachmentType.DETACHED_SIGNATURE,
                            documentSize(detachedSignature)
                    ),
                    detachedSignature.openStream(),
                    message.identifier()
            );
        }

        for (var attachment : container.businessContent().getAttachments()) {
            persistAttachment(
                    newAttachmentMetadata(
                            attachment.getName(),
                            attachment.getMimeType().getMimeTypeString(),
                            ConnectorAttachmentType.ATTACHMENT,
                            documentSize(attachment)
                    ),
                    attachment.openStream(),
                    message.identifier()
            );
        }
    }

    private ConnectorMessageAttachment newAttachmentMetadata(
            String name,
            String contentType,
            ConnectorAttachmentType type,
            int size) {
        var identifier = UUID.randomUUID() + "_" + name;
        return ConnectorMessageAttachment.builder()
                                         .identifier(identifier)
                                         .name(name)
                                         .contentType(contentType)
                                         .size(size)
                                         .description("Unzipped ASiC-S container data")
                                         .storage(ConnectorAttachmentStorage.S3_BUCKET)
                                         .type(type)
                                         .build();
    }

    /**
     * Uploads to storage first, then writes the DB record. This ordering prevents orphaned metadata
     * rows if the upload fails.
     */
    private void persistAttachment(
            ConnectorMessageAttachment attachment,
            InputStream inputStream,
            String messageIdentifier) throws IOException {
        // TODO: use a streaming save for large files rather than readAllBytes()
        fileStorageProvider.save(attachment, inputStream.readAllBytes());
        attachmentRepository.save(attachment);
        attachmentRepository.attachToMessage(attachment.identifier(), messageIdentifier);
    }

    /**
     * Returns the byte length of a DSS document. Only {@link InMemoryDocument} is supported until
     * large-file handling is implemented.
     */
    private int documentSize(DSSDocument document) {
        if (!(document instanceof InMemoryDocument inMemory)) {
            throw new ConnectorContainerException(
                    "documentSize is not yet supported for " + document.getClass().getSimpleName()
                    + " — only InMemoryDocument is currently handled");
        }
        // TODO: adapt to large-file support using an appropriate DSSDocument implementation
        return inMemory.getBytes().length;
    }
}
