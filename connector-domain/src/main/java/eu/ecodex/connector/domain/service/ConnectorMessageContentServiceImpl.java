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
import eu.ecodex.connector.domain.api.service.ConnectorMessageContentService;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageContent;
import eu.ecodex.connector.domain.spi.ConnectorMessageContentRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of the {@link ConnectorMessageContentService}.
 */
@Slf4j
@DomainService
public class ConnectorMessageContentServiceImpl implements ConnectorMessageContentService {
    private final ConnectorMessageContentRepository contentRepository;

    public ConnectorMessageContentServiceImpl(ConnectorMessageContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Override
    public ConnectorMessageContent register(@NonNull ConnectorMessageContent content) {
        log.debug("saving connector message content: [{}]", content);

        return this.contentRepository.save(content);
    }
}
