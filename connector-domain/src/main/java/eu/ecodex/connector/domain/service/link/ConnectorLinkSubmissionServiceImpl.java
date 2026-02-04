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


import static eu.ecodex.connector.domain.model.message.ConnectorMessageDirectionType.BACKEND;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.api.link.ConnectorLinkSubmissionService;
import eu.ecodex.connector.domain.api.link.ConnectorLinkTransportStrategy;
import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerSubmissionException;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of the {@link ConnectorLinkSubmissionService} interface responsible for handling
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
@DomainService
public class ConnectorLinkSubmissionServiceImpl implements ConnectorLinkSubmissionService {
    private final ConnectorEventPublisher backendLinkEventPublisher;
    private final ConnectorEventPublisher gatewayLinkEventPublisher;

    public ConnectorLinkSubmissionServiceImpl(
            ConnectorEventPublisher connectorBackendLinkEventPublisher,
            ConnectorEventPublisher connectorGatewayLinkEventPublisher) {
        this.backendLinkEventPublisher = connectorBackendLinkEventPublisher;
        this.gatewayLinkEventPublisher = connectorGatewayLinkEventPublisher;
    }

    @Override
    public void submit(@NonNull ConnectorMessage message) {
        log.debug("submitting connector message to link partner: [{}]", message);

        var direction = message.direction();
        if (direction.getTarget() == BACKEND) {
            if (StringUtils.isEmpty(message.backendName())) {
                throw new ConnectorLinkPartnerSubmissionException("unknown backend client name");
            }
            backendLinkEventPublisher.publish(message);
        } else {
            if (StringUtils.isEmpty(message.gatewayName())) {
                throw new ConnectorLinkPartnerSubmissionException("unknown gateway client name");
            }
            gatewayLinkEventPublisher.publish(message);
        }
    }
}
