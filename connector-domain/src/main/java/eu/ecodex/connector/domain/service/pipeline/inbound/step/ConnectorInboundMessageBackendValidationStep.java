/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.inbound.step;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.ConnectorMessageRoutingService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.property.routing.ConnectorMessageRoutingRule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Performs backend name validation for inbound connector messages as part of the message processing
 * workflow.
 *
 * <p>The validation logic is delegated to the {@code ConnectorMessageService}, which encapsulates
 * the necessary logic for checking the backend name.
 *
 * <p>Thread Safety:
 * This class is thread-safe, assuming the injected {@code ConnectorMessageService} is thread-safe,
 * as it does not maintain or modify any mutable state.
 */
@Slf4j
@DomainService
public class ConnectorInboundMessageBackendValidationStep implements ConnectorMessageStep {
    private final ConnectorMessageService messageService;
    private final ConnectorMessageRoutingService messageRoutingService;

    /**
     * Constructs a new instance of the {@code ConnectorInboundMessageBackendValidationStep} class.
     *
     * @param messageService the service responsible for managing and validating connector messages,
     *                       including the backend name validation process.
     */
    public ConnectorInboundMessageBackendValidationStep(
            ConnectorMessageService messageService,
            ConnectorMessageRoutingService messageRoutingService) {
        this.messageService = messageService;
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
            return inboundMessage.toBuilder().backendName(resolvedBackendName).build();
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

        return inboundMessage
                .toBuilder()
                .backendName(resolvedBackendName)
                .build();
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

        var conversationIdMessages = messageService.findByConversationIdentifier(conversationId);

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
