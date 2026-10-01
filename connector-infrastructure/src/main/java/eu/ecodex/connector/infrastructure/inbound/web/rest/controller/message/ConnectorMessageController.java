/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.message;

import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageCommand;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageReceiver;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageCommand;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageReceiver;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.infrastructure.inbound.web.ConnectorBackendClientVerifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorOutboundMessageDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorEvidenceMessageDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.parser.ConnectorRestOutboundMessageParser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageAS4Properties;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.evidence.ConnectorEvidenceTriggerMessageRequest;
import java.io.IOException;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing messages within the connector system.
 */
@RestController
public class ConnectorMessageController implements ConnectorMessageApi {
    private final ConnectorOutboundBusinessMessageReceiver outboundBusinessMessageReceiverService;
    private final ConnectorOutboundEvidenceMessageReceiver outboundEvidenceMessageReceiverService;
    private final ConnectorBackendClientVerifier backendClientVerifierService;
    private final ConnectorRestOutboundMessageParser restOutboundMessageParser;

    /**
     * Constructs a new instance of ConnectorMessageController.
     *
     * @param outboundBusinessMessageReceiverService The service responsible for receiving and
     *                                               managing outbound messages.
     * @param outboundEvidenceMessageReceiverService The service responsible for processing outbound
     *                                               evidence messages.
     * @param backendClientVerifierService           The service used for verifying backend
     *                                               clients.
     * @param restOutboundMessageParser              The rest outbound message parsing util.
     */
    public ConnectorMessageController(
        ConnectorOutboundBusinessMessageReceiver outboundBusinessMessageReceiverService,
        ConnectorOutboundEvidenceMessageReceiver outboundEvidenceMessageReceiverService,
        ConnectorBackendClientVerifier backendClientVerifierService,
        ConnectorRestOutboundMessageParser restOutboundMessageParser) {
        this.outboundBusinessMessageReceiverService = outboundBusinessMessageReceiverService;
        this.outboundEvidenceMessageReceiverService = outboundEvidenceMessageReceiverService;
        this.backendClientVerifierService = backendClientVerifierService;
        this.restOutboundMessageParser = restOutboundMessageParser;
    }

    @Override
    public ConnectorOutboundMessageDto submitOutboundMessage(
        ConnectorOutboundMessageRequest request) throws IOException {
        var command = toOutboundMessageCommand(request);
        var registeredMessage = outboundBusinessMessageReceiverService.execute(command);

        return ConnectorOutboundMessageDto.from(registeredMessage);
    }

    @Override
    public ConnectorEvidenceMessageDto submitEvidenceTriggerMessage(
        ConnectorEvidenceTriggerMessageRequest request) {
        // TODO current cn is fake, retrieve the certificate dn from user principal
        var backendClientName = this.backendClientVerifierService.getBackendClient("cn=alice");

        var message = ConnectorOutboundEvidenceMessageCommand
            .builder()
            .evidenceType(request.evidenceType())
            .backendMessageIdentifier(request.identifiers().backendMessageIdentifier())
            .referenceToIdentifier(request.identifiers().referenceToIdentifier())
            .backendName(backendClientName)
            .build();

        var registeredMessage = outboundEvidenceMessageReceiverService.execute(message);

        return ConnectorEvidenceMessageDto.of(registeredMessage.identifier());
    }

    private ConnectorOutboundBusinessMessageCommand toOutboundMessageCommand(
        ConnectorOutboundMessageRequest request)
        throws IOException {
        // TODO current cn is fake, retrieve the certificate dn from user principal
        var backendClientName = this.backendClientVerifierService.getBackendClient("cn=alice");
        return ConnectorOutboundBusinessMessageCommand
            .builder()
            .businessDomainIdentifier(
                restOutboundMessageParser.resolveBusinessDomainIdentifier(
                    request.businessDomainIdentifier()
                )
            )
            .backendMessageIdentifier(request.backendMessageIdentifier())
            .referenceToBackendMessageIdentifier(null)
            .backendName(backendClientName)
            .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
            .as4Properties(toDomainAS4Properties(request.as4Properties()))
            .businessContent(restOutboundMessageParser.toBusinessContent(request.businessContent()))
            .attachments(restOutboundMessageParser.toAttachments(request.attachments()))
            .build();
    }

    private ConnectorMessageAS4Properties toDomainAS4Properties(
        ConnectorOutboundMessageAS4Properties as4Properties) {
        var action = ConnectorAction
            .builder()
            .name(as4Properties.action().name())
            .build();
        var service = ConnectorService
            .builder()
            .name(as4Properties.service().name())
            .type(as4Properties.service().type())
            .build();
        var fromParty = restOutboundMessageParser.toParty(
            as4Properties.fromParty(),
            ConnectorPartyRoleType.INITIATOR
        );
        var toParty = restOutboundMessageParser.toParty(
            as4Properties.toParty(),
            ConnectorPartyRoleType.RESPONDER
        );

        return ConnectorMessageAS4Properties
            .builder()
            .originalSender(as4Properties.originalSender())
            .finalRecipient(as4Properties.finalRecipient())
            .ebmsMessageIdentifier(as4Properties.ebmsIdentifier())
            .conversationIdentifier(as4Properties.conversationIdentifier())
            .service(service)
            .action(action)
            .fromParty(fromParty)
            .toParty(toParty)
            .build();
    }
}
