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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.application.port.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageBusinessContentRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.infrastructure.outbound.security.container.ConnectorAsicContainerValidator;
import eu.ecodex.connector.infrastructure.outbound.security.container.ConnectorContainerFileDefinitions;
import eu.ecodex.connector.infrastructure.outbound.security.exception.ConnectorContainerException;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainer;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainerBusinessContent;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenDocument;
import eu.ecodex.connector.infrastructure.security.BaseContainerTest;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.InMemoryDocument;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorAsicContainerValidator")
public class ConnectorAsicContainerValidatorTest extends BaseContainerTest {
    private static final String MESSAGE_ID = "msg-001";
    private static final String ASICS_ID = "asics-attachment-id";
    private static final String XML_TOKEN_ID = "xml-token-attachment-id";

    @Mock
    private ConnectorMessageBusinessContentRepository businessContentRepository;
    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @Mock
    private ConnectorFileStorageProvider fileStorageProvider;

    // We subclass to intercept buildContainer and inject a controlled container,
    // bypassing all DSS/ZIP/XML static utility calls.
    @InjectMocks
    private ConnectorAsicContainerValidator validator;

    private ConnectorMessageAttachment asicsAttachment() {
        return ConnectorMessageAttachment
            .builder()
            .identifier(ASICS_ID)
            .type(ConnectorAttachmentType.ASICS)
            .build();
    }

    private ConnectorMessageAttachment xmlTokenAttachment() {
        return ConnectorMessageAttachment
            .builder()
            .identifier(XML_TOKEN_ID)
            .type(ConnectorAttachmentType.XML_TOKEN)
            .build();
    }

    private ConnectorMessageBusinessContent businessContent() {
        return ConnectorMessageBusinessContent
            .builder()
            .uuid(UUID.randomUUID().toString())
            .xmlContent(
                ConnectorMessageAttachment
                    .builder()
                    .identifier("xml-content-id")
                    .build()
            )
            .build();
    }

    private ConnectorBusinessMessage messageWith(
        List<ConnectorMessageAttachment> attachments
    ) {
        return ConnectorBusinessMessage
            .builder()
            .businessDomainIdentifier(
                BusinessDomainIdentifierTestFixtures
                    .createDefaultBusinessDomainIdentifier()
            )
            .identifier(MESSAGE_ID)
            .businessContent(businessContent())
            .attachments(attachments)
            .build();
    }

    private ConnectorAsicContainerValidator validatorWithContainer(
        ConnectorContainer container
    ) {
        return new ConnectorAsicContainerValidator(
            businessContentRepository,
            attachmentRepository,
            fileStorageProvider
        ) {
            @Override
            protected ConnectorContainer buildContainer(
                byte[] asicsBytes,
                byte[] xmlTokenBytes
            ) {
                return container;
            }
        };
    }

    private ConnectorContainer minimalContainer() {
        var pdfBytes = new byte[]{1, 2, 3};

        var pdfDocument = new InMemoryDocument(
            pdfBytes,
            ConnectorContainerFileDefinitions.TOKEN_PDF_REF
        );

        var xmlTokenDocument = new InMemoryDocument(
            "<token/>".getBytes(),
            ConnectorContainerFileDefinitions.TOKEN_XML_REF
        );

        var token = mock(ConnectorToken.class);

        var businessContent = new ConnectorContainerBusinessContent();

        return new ConnectorContainer(
            businessContent,
            token,
            xmlTokenDocument,
            pdfDocument,
            null
        );
    }

    private ConnectorContainer containerWithBusinessDocument() {
        var tokenDoc = mock(ConnectorTokenDocument.class);
        when(tokenDoc.getFilename()).thenReturn("business-document.xml");

        var token = mock(ConnectorToken.class);
        when(token.getDocument()).thenReturn(tokenDoc);

        var businessDssDocument = new InMemoryDocument(
            "<business/>".getBytes(),
            "business-document.xml",
            MimeTypeEnum.XML
        );

        var businessContent = new ConnectorContainerBusinessContent();
        businessContent.setDocument(businessDssDocument);

        var pdfDocument = new InMemoryDocument(
            new byte[]{1, 2, 3},
            ConnectorContainerFileDefinitions.TOKEN_PDF_REF
        );

        var xmlTokenDocument = new InMemoryDocument(
            "<token/>".getBytes(),
            ConnectorContainerFileDefinitions.TOKEN_XML_REF
        );

        return new ConnectorContainer(
            businessContent,
            token,
            xmlTokenDocument,
            pdfDocument,
            null
        );
    }

    private ConnectorContainer containerWithDetachedSignature() {
        var tokenDoc = mock(ConnectorTokenDocument.class);
        when(tokenDoc.getFilename()).thenReturn("business-document.xml");

        var token = mock(ConnectorToken.class);
        when(token.getDocument()).thenReturn(tokenDoc);

        var businessContent = businessContentWithDetachedSignature();

        var pdfDocument = new InMemoryDocument(
            new byte[]{1, 2, 3},
            ConnectorContainerFileDefinitions.TOKEN_PDF_REF
        );

        var xmlTokenDocument = new InMemoryDocument(
            "<token/>".getBytes(),
            ConnectorContainerFileDefinitions.TOKEN_XML_REF
        );

        return new ConnectorContainer(
            businessContent,
            token,
            xmlTokenDocument,
            pdfDocument,
            null
        );
    }

    private ConnectorContainerBusinessContent businessContentWithDetachedSignature() {
        var businessDssDocument = new InMemoryDocument(
            "<business/>".getBytes(),
            "business-document.xml",
            MimeTypeEnum.XML
        );

        var signatureDocument = new InMemoryDocument(
            new byte[]{9, 8, 7},
            "signature.xml",
            MimeTypeEnum.XML
        );

        var businessContent = new ConnectorContainerBusinessContent();
        businessContent.setDocument(businessDssDocument);
        businessContent.setDetachedSignature(signatureDocument);

        return businessContent;
    }

    private void stubContainerAttachments() {
        when(fileStorageProvider.findByIdentifier(ASICS_ID))
            .thenReturn(new byte[]{1});
        when(fileStorageProvider.findByIdentifier(XML_TOKEN_ID))
            .thenReturn(new byte[]{2});
    }

    @Nested
    @DisplayName("message validation")
    class MessageValidation {
        @Test
        void should_reject_null_message() {
            assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(
                fileStorageProvider,
                attachmentRepository
            );
        }

        @Test
        void should_reject_message_with_null_attachments() {
            var message = ConnectorBusinessMessage
                .builder()
                .businessDomainIdentifier(
                    BusinessDomainIdentifierTestFixtures
                        .createDefaultBusinessDomainIdentifier()
                )
                .identifier(MESSAGE_ID)
                .businessContent(businessContent())
                .attachments(null)
                .build();

            assertThatThrownBy(() -> validator.validate(message))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(fileStorageProvider);
        }

        @Test
        void should_reject_message_with_no_attachments() {
            assertThatThrownBy(() -> validator.validate(messageWith(List.of())))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(fileStorageProvider);
        }

        @Test
        void should_reject_message_without_asics_attachment() {
            var message = messageWith(List.of(xmlTokenAttachment()));

            assertThatThrownBy(() -> validator.validate(message))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(fileStorageProvider);
        }

        @Test
        void should_reject_message_without_xml_token_attachment() {
            var message = messageWith(List.of(asicsAttachment()));

            assertThatThrownBy(() -> validator.validate(message))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(fileStorageProvider);
        }
    }

    @Nested
    @DisplayName("container processing")
    class ContainerProcessing {
        @Test
        void should_load_asics_and_xml_token_from_storage() {
            validator = validatorWithContainer(minimalContainer());

            stubContainerAttachments();

            validator.validate(
                messageWith(List.of(
                    asicsAttachment(),
                    xmlTokenAttachment()
                ))
            );

            verify(fileStorageProvider).findByIdentifier(ASICS_ID);
            verify(fileStorageProvider).findByIdentifier(XML_TOKEN_ID);
        }

        @Test
        void should_persist_pdf_token() {
            validator = validatorWithContainer(minimalContainer());

            stubContainerAttachments();

            validator.validate(
                messageWith(List.of(
                    asicsAttachment(),
                    xmlTokenAttachment()
                ))
            );

            verify(attachmentRepository).save(argThat(attachment ->
                                                          attachment.type()
                                                              == ConnectorAttachmentType.PDF_TOKEN
                                                              && "application/pdf".equals(attachment.contentType())
            ));

            verify(attachmentRepository).attachToMessage(
                argThat(id ->
                            id.contains(ConnectorContainerFileDefinitions.TOKEN_PDF.name())
                ),
                eq(MESSAGE_ID)
            );
        }

        @Test
        void should_persist_business_document_when_present() {
            validator = validatorWithContainer(
                containerWithBusinessDocument()
            );

            stubContainerAttachments();

            validator.validate(
                messageWith(List.of(
                    asicsAttachment(),
                    xmlTokenAttachment()
                ))
            );

            verify(attachmentRepository).save(argThat(attachment ->
                                                          attachment.type()
                                                              == ConnectorAttachmentType.BUSINESS_DOCUMENT
            ));

            verify(businessContentRepository).assignBusinessDocument(
                any(),
                argThat(document ->
                            document.detachedSignature() == null
                )
            );
        }

        @Test
        void should_not_persist_business_document_for_evidence_message() {
            validator = validatorWithContainer(minimalContainer());

            stubContainerAttachments();

            validator.validate(
                messageWith(List.of(
                    asicsAttachment(),
                    xmlTokenAttachment()
                ))
            );

            verifyNoInteractions(businessContentRepository);
        }

        @Test
        void should_persist_detached_signature_when_present() {
            validator = validatorWithContainer(
                containerWithDetachedSignature()
            );

            stubContainerAttachments();

            validator.validate(
                messageWith(List.of(
                    asicsAttachment(),
                    xmlTokenAttachment()
                ))
            );

            verify(attachmentRepository).save(argThat(attachment ->
                                                          attachment.type()
                                                              == ConnectorAttachmentType.DETACHED_SIGNATURE
            ));

            verify(businessContentRepository).assignBusinessDocument(
                any(),
                argThat(document ->
                            document.detachedSignature() != null
                )
            );
        }
    }

    @Nested
    @DisplayName("container building failures")
    class ContainerBuildingFailures {
        @Test
        void should_wrap_io_exception_as_container_exception() {
            validator = new ConnectorAsicContainerValidator(
                businessContentRepository,
                attachmentRepository,
                fileStorageProvider
            ) {
                @Override
                protected ConnectorContainer buildContainer(
                    byte[] asicsBytes,
                    byte[] xmlTokenBytes
                ) throws IOException {
                    throw new IOException("simulated I/O failure");
                }
            };

            stubContainerAttachments();

            assertThatThrownBy(() ->
                                   validator.validate(
                                       messageWith(List.of(
                                           asicsAttachment(),
                                           xmlTokenAttachment()
                                       ))
                                   )
            )
                .isInstanceOf(ConnectorContainerException.class)
                .hasCauseInstanceOf(IOException.class);
        }

        @Test
        void should_wrap_jaxb_exception_as_container_exception() {
            validator = new ConnectorAsicContainerValidator(
                businessContentRepository,
                attachmentRepository,
                fileStorageProvider
            ) {
                @Override
                protected ConnectorContainer buildContainer(
                    byte[] asicsBytes,
                    byte[] xmlTokenBytes
                ) throws JAXBException {
                    throw new JAXBException("simulated JAXB failure");
                }
            };

            stubContainerAttachments();

            assertThatThrownBy(() ->
                                   validator.validate(
                                       messageWith(List.of(
                                           asicsAttachment(),
                                           xmlTokenAttachment()
                                       ))
                                   )
            )
                .isInstanceOf(ConnectorContainerException.class)
                .hasCauseInstanceOf(JAXBException.class);
        }
    }
}
