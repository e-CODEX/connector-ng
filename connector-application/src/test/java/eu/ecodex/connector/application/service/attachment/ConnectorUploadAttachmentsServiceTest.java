/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.attachment;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.application.service.impl.attachement.ConnectorUploadAttachmentsService;
import eu.ecodex.connector.application.service.impl.attachement.FileUploadCommand;
import eu.ecodex.connector.domain.exception.ConnectorMessageAttachmentException;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorUploadAttachmentsServiceTest {
    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @Mock
    private ConnectorFileStorageProvider storageProvider;
    @InjectMocks
    private ConnectorUploadAttachmentsService uploadAttachmentsService;

    @Test
    void should_upload_attachments_successfully() throws IOException {
        var savedAttachment = MessageAttachmentTestFixtures.createAttachment();
        when(attachmentRepository.save(any())).thenReturn(savedAttachment);
        when(storageProvider.save(any(), any())).thenReturn(savedAttachment.identifier());

        var fileUploadCommand = new FileUploadCommand(
                "test_attachment.txt",
                100L,
                "text/plain",
                provideTemporaryPath()
        );
        var attachments = uploadAttachmentsService.execute(List.of(fileUploadCommand));

        assertThat(attachments).isNotNull();
        assertThat(attachments).hasSize(1);
        var attachment = attachments.getFirst();
        assertThat(attachment.identifier()).endsWith("test_attachment");
        assertThat(attachment.name()).isEqualTo("test_attachment");
        assertThat(attachment.contentType()).isEqualTo("text/plain");
        assertThat(attachment.size()).isEqualTo(100L);
    }

    @Test
    void should_throw_attachment_exception_when_uploading_attachment_if_an_io_exception_occurs() throws IOException {
        var savedAttachment = MessageAttachmentTestFixtures.createAttachment();
        when(attachmentRepository.save(any())).thenReturn(savedAttachment);
        doThrow(RuntimeException.class).when(storageProvider).save(any(), any());

        var fileUploadCommand = new FileUploadCommand(
                "test_attachment.txt",
                100L,
                "text/plain",
                provideTemporaryPath()
        );

        assertThrows(
                ConnectorMessageAttachmentException.class,
                () -> uploadAttachmentsService.execute(List.of(fileUploadCommand))
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_attachments_if_upload_commands_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> uploadAttachmentsService.execute(null)
        );
    }

    private Path provideTemporaryPath() throws IOException {
        var tempPath = Files.createTempFile("test-upload-", ".txt");
        var content = "test content";
        Files.writeString(tempPath, content, StandardOpenOption.WRITE);

        return tempPath;
    }
}
