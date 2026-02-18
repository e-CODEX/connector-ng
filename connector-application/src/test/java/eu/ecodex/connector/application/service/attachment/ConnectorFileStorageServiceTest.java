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

import eu.ecodex.connector.application.service.impl.attachement.ConnectorFileStorageService;
import eu.ecodex.connector.application.service.impl.attachement.FileUploadCommand;
import eu.ecodex.connector.domain.exception.ConnectorStorageException;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorFileStorageServiceTest {
    @Mock
    private ConnectorFileStorageProvider fileStorageProvider;
    @InjectMocks
    private ConnectorFileStorageService fileStorageService;

    @Test
    void should_store_file_successfully() {
        when(fileStorageProvider.save(any(), any(), any(), any()))
               .thenReturn("f6e85600-2dca-44df-9ccd-1a2be538355a");

        var fileUploadCommand = new FileUploadCommand(
                "fakeName.txt",
                100L,
                "text/plain",
                new InputStream() {
                    @Override
                    public int read() {
                        return 100;
                    }
                }
        );
        var identifier = fileStorageService.store(List.of(fileUploadCommand));

        assertThat(identifier).isEqualTo(List.of("f6e85600-2dca-44df-9ccd-1a2be538355a"));
    }

    @Test
    void should_throw_storage_exception_when_storing_file_if_an_io_exception_occurs() {
        doThrow(RuntimeException.class).when(fileStorageProvider).save(any(), any(), any(), any());

        var fileUploadCommand = new FileUploadCommand(
                "fakeName.txt",
                100L,
                "text/plain",
                new InputStream() {
                    @Override
                    public int read() {
                        return 100;
                    }
                }
        );

        assertThrows(
                ConnectorStorageException.class,
                () -> fileStorageService.store(List.of(fileUploadCommand))
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_storing_file_if_the_file_command_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> fileStorageService.store(null)
        );
    }
}
