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
import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerSubmissionException;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.spi.ConnectorLinkPartnerRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles events related to connector gateway link processing within the system.
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
 */
@Slf4j
@DomainService
public class ConnectorGatewayLinkEventHandler implements ConnectorEventHandler {
    private final ConnectorLinkPartnerRepository linkPartnerRepository;
    private final ConnectorLinkTransportStrategy linkTransportStrategy;

    /**
     * Constructs an instance of {@code ConnectorGatewayLinkEventHandler}.
     *
     * @param linkPartnerRepository the repository used to retrieve {@code ConnectorLinkPartner}
     *                              instances based on their names. This dependency is used to look
     *                              up the link partner associated with a given gateway name.
     * @param linkTransportStrategy the strategy responsible for transporting
     *                              {@code ConnectorMessage} instances to the identified
     *                              {@code ConnectorLinkPartner}. This handles the mechanism of
     *                              message delivery based on the partner's configuration.
     */
    public ConnectorGatewayLinkEventHandler(
            ConnectorLinkPartnerRepository linkPartnerRepository,
            ConnectorLinkTransportStrategy linkTransportStrategy) {
        this.linkPartnerRepository = linkPartnerRepository;
        this.linkTransportStrategy = linkTransportStrategy;
    }

    @Override
    public void handle(@NonNull ConnectorMessage message) {
        log.debug("processing gateway message for: [{}]", message);

        var linkPartnerName = new ConnectorLinkPartnerName(message.gatewayName());
        var linkPartner = this.linkPartnerRepository.findByName(linkPartnerName);

        if (linkPartner != null) {
            log.debug(
                    "gateway message processed for: [{}] with link partner: [{}]",
                    message, linkPartner
            );

            this.linkTransportStrategy.process(message, linkPartner);
        } else {
            throw new ConnectorLinkPartnerSubmissionException(
                    String.format(
                            "the LinkPartner with name [%s] could not be found!",
                            linkPartnerName
                    ));
        }
    }
}
