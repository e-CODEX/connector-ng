/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.configuration;

import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorBusinessDocumentPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorBusinessDomainPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorContainerPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorEvidencesPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorLinkPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorMessageProcessingPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorMessageRoutingPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorQueuePropertiesDto;
import eu.ecodex.connector.infrastructure.property.ConnectorMessageProcessingProperties;
import eu.ecodex.connector.infrastructure.property.ConnectorQueueProperties;
import eu.ecodex.connector.infrastructure.property.businessdocument.ConnectorBusinessDocumentProperties;
import eu.ecodex.connector.infrastructure.property.businessdomain.ConnectorBusinessDomainProperties;
import eu.ecodex.connector.infrastructure.property.container.ConnectorContainerProperties;
import eu.ecodex.connector.infrastructure.property.evidence.ConnectorEvidencesProperties;
import eu.ecodex.connector.infrastructure.property.link.ConnectorLinkProperties;
import eu.ecodex.connector.infrastructure.property.routing.ConnectorMessageRoutingProperties;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing configurations within the connector system.
 */
@RestController
public class ConnectorConfigurationAdminController implements ConnectorConfigurationAdminApi {
    private final ConnectorBusinessDomainProperties businessDomainProperties;
    private final ConnectorContainerProperties containerProperties;
    private final ConnectorQueueProperties queueProperties;
    private final ConnectorMessageProcessingProperties messageProcessingProperties;
    private final ConnectorEvidencesProperties evidencesProperties;
    private final ConnectorBusinessDocumentProperties businessDocumentProperties;
    private final ConnectorMessageRoutingProperties messageRoutingProperties;
    private final ConnectorLinkProperties linkProperties;

    /**
     * Constructs a new instance of {@code ConnectorConfigurationAdminController}.
     *
     * @param businessDomainProperties    the properties related to business domains
     * @param containerProperties         the properties related to the container configuration
     * @param queueProperties             the properties related to queue management
     * @param messageProcessingProperties the properties for message processing configuration
     * @param evidencesProperties         the properties associated with evidence management
     * @param businessDocumentProperties  the properties for business document handling
     * @param messageRoutingProperties    the properties for message routing configurations
     * @param linkProperties              the properties for backend link partners configuration
     */
    public ConnectorConfigurationAdminController(
            ConnectorBusinessDomainProperties businessDomainProperties,
            ConnectorContainerProperties containerProperties,
            ConnectorQueueProperties queueProperties,
            ConnectorMessageProcessingProperties messageProcessingProperties,
            ConnectorEvidencesProperties evidencesProperties,
            ConnectorBusinessDocumentProperties businessDocumentProperties,
            ConnectorMessageRoutingProperties messageRoutingProperties,
            ConnectorLinkProperties linkProperties) {
        this.businessDomainProperties = businessDomainProperties;
        this.containerProperties = containerProperties;
        this.queueProperties = queueProperties;
        this.messageProcessingProperties = messageProcessingProperties;
        this.evidencesProperties = evidencesProperties;
        this.businessDocumentProperties = businessDocumentProperties;
        this.messageRoutingProperties = messageRoutingProperties;
        this.linkProperties = linkProperties;
    }

    @Override
    public ConnectorBusinessDomainPropertiesDto listBusinessDomains() {
        return ConnectorBusinessDomainPropertiesDto.builder()
                                                   .defaults(businessDomainProperties.getDefaults())
                                                   .build();
    }

    @Override
    public ConnectorContainerPropertiesDto listContainer() {
        return ConnectorContainerPropertiesDto.builder()
                                              .signature(containerProperties.getSignature())
                                              .build();
    }

    @Override
    public ConnectorQueuePropertiesDto listQueues() {
        return ConnectorQueuePropertiesDto
                .builder()
                .outboundMessageStagingQueue(queueProperties.getOutboundMessageStagingQueue())
                .outboundEvidenceTriggerQueue(queueProperties.getOutboundEvidenceTriggerQueue())
                .outboundMessageProcessingQueue(queueProperties.getOutboundMessageProcessingQueue())
                .inboundMessageProcessingQueue(queueProperties.getInboundMessageProcessingQueue())
                .inboundEvidenceTriggerQueue(queueProperties.getInboundEvidenceTriggerQueue())
                .backendDeliveryQueue(queueProperties.getBackendDeliveryQueue())
                .gatewaySubmissionQueue(queueProperties.getGatewaySubmissionQueue())
                .gatewaySubmissionReplyQueue(queueProperties.getGatewaySubmissionReplyQueue())
                .build();
    }

    @Override
    public ConnectorMessageProcessingPropertiesDto listMessageProcessing() {
        return ConnectorMessageProcessingPropertiesDto
                .builder()
                .ebmsIdGeneratorEnabled(messageProcessingProperties.isEbmsIdGeneratorEnabled())
                .identifierSuffix(messageProcessingProperties.getIdentifierSuffix())
                .ebmsIdSuffix(messageProcessingProperties.getEbmsIdSuffix())
                .transportIdSuffix(messageProcessingProperties.getTransportIdSuffix())
                .outboundMessageVerificationMode(
                        messageProcessingProperties.getOutboundMessageVerificationMode())
                .inboundMessageVerificationMode(
                        messageProcessingProperties.getInboundMessageVerificationMode())
                .sendGeneratedEvidencesToBackend(
                        messageProcessingProperties.isSendGeneratedEvidencesToBackend())
                .build();
    }

    @Override
    public ConnectorEvidencesPropertiesDto listEvidences() {
        return ConnectorEvidencesPropertiesDto.builder()
                                              .signature(evidencesProperties.getSignature())
                                              .issuer(evidencesProperties.getIssuer())
                                              .build();
    }

    @Override
    public ConnectorBusinessDocumentPropertiesDto listBusinessDocument() {
        return ConnectorBusinessDocumentPropertiesDto
                .builder()
                .country(businessDocumentProperties.getCountry())
                .serviceProvider(businessDocumentProperties.getServiceProvider())
                .defaultAdvancedSystemType(
                        businessDocumentProperties.getDefaultAdvancedSystemType())
                .authenticationValidation(businessDocumentProperties.getAuthenticationValidation())
                .signature(businessDocumentProperties.getSignature())
                .build();
    }

    @Override
    public ConnectorMessageRoutingPropertiesDto listRouting() {
        return ConnectorMessageRoutingPropertiesDto
                .builder()
                .enabled(messageRoutingProperties.isEnabled())
                .defaultBackendName(messageRoutingProperties.getDefaultBackendName())
                .backendRules(messageRoutingProperties.getBackendRules())
                .build();
    }

    @Override
    public ConnectorLinkPropertiesDto listBackendLinkPartners() {
        return ConnectorLinkPropertiesDto.builder()
                                         .backend(linkProperties.getBackend())
                                         .build();
    }
}
