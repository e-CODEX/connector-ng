/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.messaging.publisher;

import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.spi.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendDeliveryWebService;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.infrastructure.property.ConnectorQueueProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.jspecify.annotations.NonNull;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Backend Implementation of the {@link ConnectorEventPublisher}.
 */
@Slf4j
@Component("connectorBackendLinkEventPublisher")
public class ConnectorBackendLinkEventPublisher implements ConnectorEventPublisher {
    private final JmsTemplate jmsTemplate;
    private final ConnectorQueueProperties queueProperties;

    public ConnectorBackendLinkEventPublisher(
            JmsTemplate jmsTemplate,
            ConnectorQueueProperties queueProperties) {
        this.jmsTemplate = jmsTemplate;
        this.queueProperties = queueProperties;
    }

    @Override
    public void publish(@NonNull ConnectorMessage message) {
        log.info("Submitting message [{}] to backend link processing queue", message.identifier());
        this.jmsTemplate.convertAndSend(
                this.queueProperties.getBackendDeliveryQueue(), message
        );
    }
}
