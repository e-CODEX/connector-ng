/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.link;


import eu.ecodex.connector.application.service.usecase.link.ConnectorLinkPartnerVerifier;
import eu.ecodex.connector.application.service.usecase.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.spi.link.ConnectorLinkTransportStrategy;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorLinkSubmitter} interface responsible for handling
 * the submission of {@link ConnectorMessage} instances to appropriate connector link partners.
 *
 * <p>This service processes connector messages by determining the target link partner based on the
 * message's direction and associated partner information. It retrieves the corresponding
 * {@link ConnectorLinkPartner} and delegates the message transport to the configured
 * {@link ConnectorLinkTransportStrategy}.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li> Ensure messages are submitted only if valid partner information is provided in
 *     the message.
 *     <li> Manage errors arising from missing or invalid partner definitions, ensuring robust
 *     system behaviour.
 *     <li> Abstract the message transport mechanism to a strategy pattern for flexibility.
 * </ul>
 */
@Slf4j
@Service
public class ConnectorLinkSubmitterService implements ConnectorLinkSubmitter {
    private final ConnectorLinkPartnerVerifier linkVerifier;
    private final ConnectorLinkTransportStrategy linkTransportStrategy;

    public ConnectorLinkSubmitterService(
            ConnectorLinkPartnerVerifier linkVerifier,
            ConnectorLinkTransportStrategy linkTransportStrategy) {
        this.linkVerifier = linkVerifier;
        this.linkTransportStrategy = linkTransportStrategy;
    }

    @Override
    public void submit(@NonNull ConnectorMessage message) {
        log.debug("submitting connector message to link partner: [{}]", message);

        this.linkVerifier.verify(message);
        this.linkTransportStrategy.transport(message);
    }
}
