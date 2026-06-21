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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorMessageDocumentTestFixtures;
import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.infrastructure.security.BaseContainerTest;
import eu.ecodex.connector.infrastructure.security.SecurityUtil;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class ConnectorAsicContainerBuilderTest extends BaseContainerTest {
    @Autowired
    private ConnectorAsicContainerBuilder asicContainerBuilder;
    @MockitoBean
    private ConnectorFileStorageProvider fileStorageProvider;

    @Test
    void should_create_asics_container_and_sign_it_successfully() {
        when(fileStorageProvider.findByIdentifier(any()))
                .thenReturn(FileTestFixtures.readAsBytes("raw/document/NonSigned.pdf"))
                .thenReturn(FileTestFixtures.readAsBytes("raw/test-xml.xml"));

        var message = MessageTestFixtures
                .createOutboundBusinessMessage()
                .toBuilder()
                .businessContent(
                        MessageContentTestFixtures
                                .createContent()
                                .toBuilder()
                                .xmlContent(MessageAttachmentTestFixtures.createBusinessContentAttachment())
                                .businessDocument(
                                        ConnectorMessageDocumentTestFixtures
                                                .createDocumentWithoutSignature()
                                )
                                .build()
                )
                .build();

        var container = asicContainerBuilder.createAsicContainer(message);

        assertThat(container).isNotNull();
        assertThat(container.token()).isNotNull();
        assertThat(container.tokenXML()).isNotNull();
        assertThat(container.tokenPDF()).isNotNull();
        assertThat(container.asicDocument()).isNotNull();
        assertThat(container.asicDocument().getMimeType()).isEqualTo(MimeTypeEnum.ASICS);
        assertThat(SecurityUtil.hasSignature(container.asicDocument())).isTrue();
    }
}
