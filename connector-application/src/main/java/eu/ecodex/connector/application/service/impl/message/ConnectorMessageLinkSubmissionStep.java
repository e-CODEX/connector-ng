/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message;

import eu.ecodex.connector.domain.api.link.ConnectorLinkSubmissionService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

/**
 * {@link ConnectorMessageStep} responsible for submitting a {@link ConnectorMessage} to a link
 * partner.
 *
 * <p>This step is part of the connector message processing pipeline. It delegates
 * the submission of the message to {@link ConnectorLinkSubmissionService}, which handles the
 * communication with the external link partner.
 *
 * <p>The original message is returned unchanged to allow further processing in the pipeline.
 */
@Slf4j
@Component
public class ConnectorMessageLinkSubmissionStep implements ConnectorMessageStep {
    private final ConnectorLinkSubmissionService linkSubmissionService;

    public ConnectorMessageLinkSubmissionStep(
            ConnectorLinkSubmissionService linkSubmissionService) {
        this.linkSubmissionService = linkSubmissionService;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage message) {
        log.debug("submitting message [{}] to link partner", message);

        // TODO uncomment below once link management is implemented
        // this.linkSubmissionService.submit(message);

        log.debug("message submitted [{}] to link partner", message);

        return message;
    }
}
