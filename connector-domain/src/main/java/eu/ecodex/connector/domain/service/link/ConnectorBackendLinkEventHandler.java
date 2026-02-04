/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.link;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.ConnectorEventHandler;
import eu.ecodex.connector.domain.api.link.ConnectorLinkTransportStrategy;
import eu.ecodex.connector.domain.api.service.ConnectorLinkService;
import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerSubmissionException;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles events related to connector backend link processing within the system.
 *
 * <p>The {@code ConnectorBackendLinkEventHandler} class is responsible for processing
 * {@link ConnectorMessage} instances received from the backend, identifying the appropriate
 * {@link ConnectorLinkPartner}, and delegating the message to the specified
 * {@link ConnectorLinkTransportStrategy} for further processing.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li> Retrieves the {@link ConnectorLinkPartner} associated with the backend name in
 *     the message.
 *     <li> Delegates the message for processing using the defined transport strategy.
 *     <li> Throws an exception if no link partner is found for the given backend name.
 * </ul>
 *
 * <p>Thread Safety:
 * This class is thread-safe provided that its dependencies, {@link ConnectorLinkService} and
 * {@link ConnectorLinkTransportStrategy}, are thread-safe.
 */
@Slf4j
@DomainService
public class ConnectorBackendLinkEventHandler implements ConnectorEventHandler {
    private final ConnectorLinkService linkService;
    private final ConnectorLinkTransportStrategy linkTransportStrategy;

    public ConnectorBackendLinkEventHandler(
            ConnectorLinkService linkService,
            ConnectorLinkTransportStrategy linkTransportStrategy) {
        this.linkService = linkService;
        this.linkTransportStrategy = linkTransportStrategy;
    }

    @Override
    public void handle(@NonNull ConnectorMessage message) {
        log.debug("processing backend message for: [{}]", message);

        var linkPartnerName = new ConnectorLinkPartnerName(message.backendName());
        var linkPartner = this.linkService.getByLinkPartnerName(linkPartnerName);

        if (linkPartner != null) {
            log.debug(
                    "backend message processed for: [{}] with link partner: [{}]",
                    message, linkPartner
            );

            this.linkTransportStrategy.process(message, linkPartner);
        } else {
            throw new ConnectorLinkPartnerSubmissionException(String.format(
                    "the LinkPartner with name [%s] could not be found!", linkPartnerName
            ));
        }
    }
}
