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
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.infrastructure.property.ConnectorQueueProperties;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Session;
import java.util.ArrayList;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Gateway Implementation of the {@link ConnectorEventPublisher}.
 */
@Slf4j
@Component("connectorGatewayLinkEventPublisher")
public class ConnectorGatewayLinkEventPublisher implements ConnectorEventPublisher {
    private final JmsTemplate jmsTemplate;
    private final ConnectorFileStorageProvider fileStorageProvider;
    private final ConnectorQueueProperties queueProperties;

    /**
     * Creates a new gateway event publisher.
     *
     * @param jmsTemplate         JMS template used to send messages to the gateway queues
     * @param fileStorageProvider provider used to access stored message files or attachments
     * @param queueProperties     configuration containing the JMS queue destinations
     */
    public ConnectorGatewayLinkEventPublisher(
            JmsTemplate jmsTemplate,
            ConnectorFileStorageProvider fileStorageProvider,
            ConnectorQueueProperties queueProperties) {
        this.jmsTemplate = jmsTemplate;
        this.fileStorageProvider = fileStorageProvider;
        this.queueProperties = queueProperties;
    }

    @Override
    public void publish(@NonNull ConnectorMessage message) {
        log.info("submitting message [{}] to gateway link", message);

        this.jmsTemplate.send(
                queueProperties.getGatewaySubmissionQueue(),
                session -> toMapMessage(message, session)
        );
    }

    private MapMessage toMapMessage(ConnectorMessage message, Session session) throws JMSException {
        var mapMessage = session.createMapMessage();

        mapMessage.setStringProperty("messageType", "submitMessage");
        mapMessage.setStringProperty("messageId", message.identifier());

        var as4Properties = message.as4Properties();

        mapMessage.setStringProperty("originalSender", as4Properties.originalSender());
        mapMessage.setStringProperty("finalRecipient", as4Properties.finalRecipient());

        var service = as4Properties.service();
        mapMessage.setStringProperty("service", Objects.requireNonNull(service).name());
        mapMessage.setStringProperty("serviceType", service.type());

        var action = as4Properties.action();
        mapMessage.setStringProperty("action", Objects.requireNonNull(action).name());

        var fromParty = as4Properties.fromParty();
        mapMessage.setStringProperty("fromPartyId", Objects.requireNonNull(fromParty).identifier());
        mapMessage.setStringProperty("fromPartyType", fromParty.identifierType());
        mapMessage.setStringProperty("fromRole", fromParty.role());
        var toParty = as4Properties.toParty();
        mapMessage.setStringProperty("toPartyId", Objects.requireNonNull(toParty).identifier());
        mapMessage.setStringProperty("toPartyType", toParty.identifierType());
        mapMessage.setStringProperty("toRole", toParty.role());

        if (as4Properties.conversationIdentifier() != null) {
            mapMessage.setStringProperty("conversationId", as4Properties.conversationIdentifier());
        }

        if (as4Properties.referenceToIdentifier() != null) {
            mapMessage.setStringProperty("refToMessageId", as4Properties.referenceToIdentifier());
        }

        buildAttachments(mapMessage, message);

        return mapMessage;
    }

    private void buildAttachments(
            MapMessage mapMessage, ConnectorMessage message) throws JMSException {
        // TODO with version to 5.2 of the gateway, see how to attach S3 Id instead of byte[]
        var attachments = new ArrayList<ConnectorMessageAttachment>();
        attachments.add(Objects.requireNonNull(
                Objects.requireNonNull(message.businessContent()).businessDocument()).attachment());
        attachments.addAll(message.attachments());

        mapMessage.setStringProperty("totalNumberOfPayloads", attachments.size() + "");
        mapMessage.setBooleanProperty("putAttachmentInQueue", false);

        var counter = 1;

        for (var attachment : attachments) {
            mapMessage.setStringProperty(
                    String.format("payload_%s_mimeContentId", counter), attachment.identifier());
            mapMessage.setStringProperty(
                    String.format("payload_%s_mimeType", counter), attachment.contentType());
            mapMessage.setStringProperty(
                    String.format("payload_%s_description", counter), attachment.description());
            mapMessage.setStringProperty(
                    String.format("payload_%s_fileName", counter), attachment.name());

            var payload = this.fileStorageProvider.findByIdentifier(attachment.identifier());
            mapMessage.setBytes(String.format("payload_%s", counter), payload);

            counter++;
        }
    }
}
