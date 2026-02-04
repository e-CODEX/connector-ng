/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.service.ConnectorMessageAttachmentService;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of the {@link ConnectorMessageAttachmentService}.
 */
@Slf4j
@DomainService
public class ConnectorMessageAttachmentServiceImpl implements ConnectorMessageAttachmentService {
    private final ConnectorMessageAttachmentRepository attachmentRepository;

    /**
     * Creates an instance of {@code ConnectorMessageAttachmentServiceImpl}.
     *
     * @param attachmentRepository the repository used for managing
     *                             {@link ConnectorMessageAttachment} entities.
     */
    public ConnectorMessageAttachmentServiceImpl(
            ConnectorMessageAttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public ConnectorMessageAttachment register(@NonNull ConnectorMessageAttachment attachment) {
        log.debug("saving connector message attachment: [{}]", attachment);

        return this.attachmentRepository.save(attachment);
    }
}
