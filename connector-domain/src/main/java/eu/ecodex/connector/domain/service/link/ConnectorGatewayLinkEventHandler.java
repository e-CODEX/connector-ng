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
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * A handler for processing connector messages and routing them to the appropriate link partner.
 *
 * <p>This class implements the {@link ConnectorEventHandler} interface and provides functionality
 * for handling {@link ConnectorMessage} instances by identifying the corresponding link partner and
 * executing the specified transport strategy. It relies on the {@link ConnectorLinkService} to
 * retrieve link partner configurations and the {@link ConnectorLinkTransportStrategy} for managing
 * message delivery.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li> Retrieve the link partner associated with the message's gateway name.
 *     <li> Process the message using the configured transport strategy for the identified link
 *     partner.
 *     <li> Throw an exception if the link partner cannot be found for the provided gateway name.
 * </ul>
 *
 * <p>Thread Safety:
 * Instances of this class are expected to be thread-safe as long as the provided dependencies
 * ({@code ConnectorLinkService} and {@code ConnectorLinkTransportStrategy}) are thread-safe.
 *
 * <p>Throws:
 * {@link ConnectorLinkPartnerSubmissionException} - if the link partner associated with the
 * provided gateway name cannot be found.
 */
@Slf4j
@DomainService
public class ConnectorGatewayLinkEventHandler implements ConnectorEventHandler {
    private final ConnectorLinkService linkService;
    private final ConnectorLinkTransportStrategy linkTransportStrategy;

    /**
     * Constructs an instance of {@code ConnectorGatewayLinkEventHandler}.
     *
     * @param linkService           the service responsible for managing connector link partners,
     *                              allowing retrieval or addition of link partner configurations.
     * @param linkTransportStrategy the strategy to handle the transport of connector messages to
     *                              designated link partners, ensuring proper communication with
     *                              external or internal systems.
     */
    public ConnectorGatewayLinkEventHandler(
            ConnectorLinkService linkService,
            ConnectorLinkTransportStrategy linkTransportStrategy) {
        this.linkService = linkService;
        this.linkTransportStrategy = linkTransportStrategy;
    }

    @Override
    public void handle(@NonNull ConnectorMessage message) {
        log.debug("processing gateway message for: [{}]", message);

        var linkPartnerName = new ConnectorLinkPartnerName(message.gatewayName());
        var linkPartner = this.linkService.getByLinkPartnerName(linkPartnerName);

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
