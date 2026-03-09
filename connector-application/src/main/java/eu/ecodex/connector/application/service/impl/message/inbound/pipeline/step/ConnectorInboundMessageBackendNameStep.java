/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message.inbound.pipeline.step;

import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingRule;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.application.service.usecase.routing.ConnectorMessageRoutingService;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * {@link ConnectorMessageStep} responsible for resolving and assigning the backend name for an
 * inbound {@link ConnectorMessage}.
 *
 * <p>This step ensures that every inbound message is associated with the correct backend system.
 * If
 * the backend name is already present on the message, the step skips processing. Otherwise, it
 * attempts to determine the backend name using the following strategy:
 *
 * <ol>
 *     <li>Resolve the backend name from existing messages belonging to the
 *     same conversation (via the conversation identifier).</li>
 *     <li>If no backend is found through the conversation, resolve it using
 *     configured routing rules for the message's business domain.</li>
 *     <li>If no routing rule matches, fall back to the default backend name
 *     defined for the business domain.</li>
 * </ol>
 *
 * <p>Once resolved, the backend name is persisted.
 */
@Slf4j
@Component
public class ConnectorInboundMessageBackendNameStep implements ConnectorMessageStep {
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageRoutingService messageRoutingService;

    /**
     * Creates a new backend name resolution step.
     *
     * @param messageRepository     repository used to retrieve and update messages
     * @param messageRoutingService service providing routing rules and default backend
     *                              configuration
     */
    public ConnectorInboundMessageBackendNameStep(
            ConnectorMessageRepository messageRepository,
            ConnectorMessageRoutingService messageRoutingService) {
        this.messageRepository = messageRepository;
        this.messageRoutingService = messageRoutingService;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage inboundMessage) {
        log.debug("processing inbound message backend name validation for: [{}]", inboundMessage);

        if (StringUtils.isNotEmpty(inboundMessage.backendName())) {
            log.debug(
                    "backend name is already set for the message [{}], skipping the name "
                    + "validation", inboundMessage
            );

            return inboundMessage;
        }

        String resolvedBackendName;

        final var conversationId = inboundMessage.as4Properties().conversationIdentifier();
        resolvedBackendName = resolveBackendNameFromConversation(inboundMessage, conversationId);

        if (resolvedBackendName != null) {
            return this.messageRepository.updateBackendName(
                    inboundMessage.identifier(), resolvedBackendName
            );
        }

        final var businessDomainIdentifier = inboundMessage.businessDomainIdentifier();
        final var defaultBackendName = messageRoutingService.getDefaultBackendName(
                businessDomainIdentifier
        );

        resolvedBackendName = resolveBackendNameFromRouting(
                inboundMessage,
                businessDomainIdentifier,
                defaultBackendName
        );

        return this.messageRepository.updateBackendName(
                inboundMessage.identifier(), resolvedBackendName
        );
    }

    private String resolveBackendNameFromConversation(
            ConnectorMessage inboundMessage, String conversationId) {
        if (StringUtils.isEmpty(conversationId)) {
            return null;
        }

        log.debug(
                "conversation uuid is set for the message [{}], setting the correct backend name",
                inboundMessage
        );

        var conversationIdMessages = messageRepository.findByConversationIdentifier(conversationId);

        String backendName = conversationIdMessages
                .stream()
                .map(ConnectorMessage::backendName)
                .filter(StringUtils::isNotEmpty)
                .findAny()
                .orElse(null);

        if (backendName != null) {
            log.debug(
                    "found backend name [{}] for the conversation uuid [{}]",
                    backendName,
                    conversationId
            );
        }

        return backendName;
    }

    private String resolveBackendNameFromRouting(
            ConnectorMessage inboundMessage,
            ConnectorBusinessDomainIdentifier businessDomainIdentifier,
            String defaultBackendName
    ) {
        if (!messageRoutingService.isRoutingEnabled(businessDomainIdentifier)) {
            log.debug(
                    "backend routing is disabled, setting default backend name: [{}]",
                    defaultBackendName
            );
            return defaultBackendName;
        }

        log.debug("backend routing is enabled, setting the correct backend name");

        var backendRoutingRules =
                messageRoutingService.getBackendRoutingRule(businessDomainIdentifier);

        return backendRoutingRules
                .values()
                .stream()
                .sorted(ConnectorMessageRoutingRule.getComparator())
                .filter(routingRule -> routingRule.getMatchClause().matches(inboundMessage))
                .map(ConnectorMessageRoutingRule::getLinkName)
                .findFirst()
                .map(backendName -> {
                    log.debug(
                            "found backend name [{}] for the message [{}]", backendName,
                            inboundMessage
                    );
                    return backendName;
                })
                .orElse(defaultBackendName);
    }
}
