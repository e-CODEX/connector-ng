/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.service.ConnectorActionService;
import eu.ecodex.connector.domain.api.service.ConnectorBusinessDomainService;
import eu.ecodex.connector.domain.api.service.ConnectorKeystoreService;
import eu.ecodex.connector.domain.api.service.ConnectorPartyService;
import eu.ecodex.connector.domain.api.service.ConnectorProcessingModeService;
import eu.ecodex.connector.domain.api.service.ConnectorServiceService;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeException;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeVerificationException;
import eu.ecodex.connector.domain.exception.NotFoundException;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.util.SecureXmlParserUtil;
import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Default Implementation of the {@link ConnectorProcessingModeService}.
 */
@Slf4j
@DomainService
public class ConnectorProcessingModeServiceImpl implements ConnectorProcessingModeService {
    private final ConnectorProcessingModeRepository processingModeRepository;
    private final ConnectorBusinessDomainService businessDomainService;
    private final ConnectorServiceService serviceService;
    private final ConnectorActionService actionService;
    private final ConnectorPartyService partyService;
    private final ConnectorKeystoreService keystoreService;

    /**
     * Constructs a new instance of {@code ConnectorProcessingModeServiceImpl} and initializes its
     * dependencies.
     *
     * @param serviceService  the service responsible for managing and retrieving service-related
     *                        data
     * @param actionService   the service responsible for managing and retrieving action-related
     *                        data
     * @param partyService    the service responsible for validating and managing party-related
     *                        data
     * @param keystoreService the service responsible for managing and retrieving keystore-related
     *                        data
     */
    public ConnectorProcessingModeServiceImpl(
            ConnectorProcessingModeRepository processingModeRepository,
            ConnectorBusinessDomainService businessDomainService,
            ConnectorServiceService serviceService,
            ConnectorActionService actionService,
            ConnectorPartyService partyService,
            ConnectorKeystoreService keystoreService) {
        this.processingModeRepository = processingModeRepository;
        this.businessDomainService = businessDomainService;
        this.serviceService = serviceService;
        this.actionService = actionService;
        this.partyService = partyService;
        this.keystoreService = keystoreService;
    }

    @Override
    public ConnectorProcessingMode register(
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
            @NonNull ConnectorProcessingMode processingMode) {
        log.debug(
                "creating new processing mode [{}] for business domain [{}]: ", processingMode,
                businessDomainIdentifier
        );

        var foundBusinessDomain = this.businessDomainService.findByIdentifier(
                businessDomainIdentifier
        );

        if (foundBusinessDomain == null) {
            throw new ConnectorBusinessDomainNotFoundException("business domain not found");
        }

        var existingProcessingMode = this.processingModeRepository.findByBusinessDomainIdentifier(
                businessDomainIdentifier
        );

        if (existingProcessingMode != null) {
            throw new ConnectorProcessingModeException(
                    "the business domain has already a processing mode linked"
            );
        }

        var parsedProcessingMode = parseXmlFile(processingMode);

        log.debug("processing mode parsed successfully [{}]", parsedProcessingMode);

        parsedProcessingMode = parsedProcessingMode
                .toBuilder()
                .businessDomain(foundBusinessDomain)
                .build();

        var createdProcessingMode = this.processingModeRepository.save(
                parsedProcessingMode,
                businessDomainIdentifier
        );

        var createdTruststore = this.keystoreService.persist(
                Objects.requireNonNull(parsedProcessingMode.truststore()),
                businessDomainIdentifier
        );

        createdProcessingMode = createdProcessingMode
                .toBuilder()
                .truststore(createdTruststore)
                .build();

        this.partyService.persistAll(
                Objects.requireNonNull(parsedProcessingMode.parties()).stream().toList(),
                businessDomainIdentifier
        );
        this.serviceService.persistAll(
                Objects.requireNonNull(parsedProcessingMode.services()).stream().toList(),
                businessDomainIdentifier
        );
        this.actionService.persistAll(
                Objects.requireNonNull(parsedProcessingMode.actions()).stream().toList(),
                businessDomainIdentifier
        );

        var updatedProcessingMode = this.updateKeystore(
                createdProcessingMode.uuid(), createdTruststore.uuid()
        );

        log.debug("processing mode [{}] created successfully", createdProcessingMode);

        return updatedProcessingMode;
    }

    @Override
    public ConnectorProcessingMode updateKeystore(
            @Nonnull String uuid, @Nonnull String keystoreUuid) {
        log.debug("updating processing mode with uuid: [{}]", uuid);

        var existingProcessingMode = this.processingModeRepository.findByUuid(uuid);

        if (existingProcessingMode == null) {
            throw new ConnectorProcessingModeNotFoundException(
                    "not found processing mode for business domain");
        }

        return this.processingModeRepository.updateKeystore(uuid, keystoreUuid);
    }

    @Override
    public void checkMessage(
            @NonNull ConnectorMessage message,
            @NonNull ProcessingModeVerificationMode verificationMode) {
        log.debug(
                "verifying message [{}] with verification mode [{}]",
                message.identifier(), verificationMode
        );

        switch (verificationMode) {
            case STRICT -> this.processStrictVerification(message);
            case RELAXED -> this.processRelaxedVerification(message);
            default -> this.processCreateVerification(message);
        }
    }

    private ConnectorProcessingMode parseXmlFile(ConnectorProcessingMode processingMode) {
        log.debug("parsing processing mode xml file");

        try {
            var document = SecureXmlParserUtil.parseSecurely(processingMode.content());

            document.getDocumentElement().normalize();

            var root = document.getDocumentElement();

            var homePartyName = root.getAttribute("party");

            var partiesNode = document.getElementsByTagName("party");
            var partyIdTypesNode = document.getElementsByTagName("partyIdType");
            var servicesNodes = document.getElementsByTagName("service");
            var actionsNodes = document.getElementsByTagName("action");

            var parties = this.retrieveParties(partiesNode, partyIdTypesNode, homePartyName);
            var services = this.retrieveServices(servicesNodes);
            var actions = this.retrieveActions(actionsNodes);

            var homeParty = parties.stream()
                                   .filter(p -> p.name().equals(homePartyName))
                                   .findFirst()
                                   .orElse(null);

            if (homeParty == null) {
                throw new ConnectorProcessingModeException("home party not found");
            }

            var updatedProcessingMode = processingMode
                    .toBuilder()
                    .parties(parties)
                    .services(services)
                    .actions(actions)
                    .build();

            log.debug("processing mode parsed successfully");

            return updatedProcessingMode;
        } catch (Exception e) {
            log.error("error parsing xml file", e);
            throw new ConnectorProcessingModeException("error parsing xml file (IOException)", e);
        }
    }

    private Map<String, String> retrievePartyIdTypes(NodeList partyIdTypesNodeList) {
        log.debug("retrieving party id types from processing mode xml file");

        var partyIdTypes = new HashMap<String, String>();

        for (int i = 0; i < partyIdTypesNodeList.getLength(); i++) {
            var partyIdType = (Element) partyIdTypesNodeList.item(i);
            var name = partyIdType.getAttribute("name");
            var value = partyIdType.getAttribute("value");

            partyIdTypes.put(name, value);
        }

        return partyIdTypes;
    }

    private HashSet<ConnectorParty> retrieveParties(
            NodeList partiesNodeList, NodeList partyIdTypesNodeList, String homePartyName) {
        log.debug("retrieving parties from processing mode xml file");

        var partyIdTypes = this.retrievePartyIdTypes(partyIdTypesNodeList);

        var parties = new HashSet<ConnectorParty>();

        for (int i = 0; i < partiesNodeList.getLength(); i++) {
            var party = (Element) partiesNodeList.item(i);

            var name = party.getAttribute("name");

            var identifier = (Element) party.getElementsByTagName("identifier").item(0);
            String partyId = identifier.getAttribute("partyId");
            String partyIdType = identifier.getAttribute("partyIdType");

            var connectorParty = ConnectorParty
                    .builder()
                    .name(name)
                    .identifier(partyId)
                    .identifierType(partyIdTypes.getOrDefault(partyIdType, null))
                    .role("GW")
                    .roleType(ConnectorPartyRoleType.INITIATOR)
                    .isHome(homePartyName.equals(name))
                    .build();

            parties.add(connectorParty);

            connectorParty = connectorParty
                    .toBuilder()
                    .roleType(ConnectorPartyRoleType.RESPONDER)
                    .build();
            parties.add(connectorParty);
        }

        return parties;
    }

    private HashSet<ConnectorService> retrieveServices(NodeList servicesNodeList) {
        log.debug("retrieving services from processing mode xml file");

        var services = new HashSet<ConnectorService>();

        for (int i = 0; i < servicesNodeList.getLength(); i++) {
            var service = (Element) servicesNodeList.item(i);

            var value = service.getAttribute("value");
            var type = service.getAttribute("type");

            var connectorParty = ConnectorService
                    .builder()
                    .name(value)
                    .type(type)
                    .build();

            services.add(connectorParty);
        }

        return services;
    }

    private HashSet<ConnectorAction> retrieveActions(NodeList actionsNodeList) {
        log.debug("retrieving actions from processing mode xml file");

        var actions = new HashSet<ConnectorAction>();

        for (int i = 0; i < actionsNodeList.getLength(); i++) {
            var service = (Element) actionsNodeList.item(i);

            var value = service.getAttribute("value");

            var connectorParty = ConnectorAction
                    .builder()
                    .name(value)
                    .build();

            actions.add(connectorParty);
        }

        return actions;
    }

    private void processStrictVerification(ConnectorMessage message) {
        this.processServiceAndActionVerification(message);
    }

    private void processRelaxedVerification(ConnectorMessage message) {
        this.processServiceAndActionVerification(message);

        var toParty = message.as4Properties().toParty();

        if (toParty != null && toParty.identifierType().isBlank()) {
            log.warn(
                    "message with uuid [{}] verification mode is RELAXED."
                    + "Assuming ToParty IdentifierType [{}] as empty!",
                    message.identifier(), message.as4Properties().toParty().identifierType()
            );
        }

        if (!this.partyService.exists(toParty, message.businessDomainIdentifier())) {
            throw new ConnectorProcessingModeVerificationException(
                    String.format(
                            "message toParty [%s] is not configured on the connector! "
                            + "Check the P-Mode linked to business domain with uuid [%s]",
                            toParty, message.businessDomainIdentifier()
                    )
            );
        }

        var fromParty = message.as4Properties().fromParty();

        if (fromParty != null && fromParty.identifierType().isBlank()) {
            log.warn(
                    "message with uuid [{}] verification mode is RELAXED."
                    + "Assuming FromParty IdentifierType [{}] as empty!",
                    message.identifier(), fromParty.identifierType()
            );
        }

        if (!this.partyService.exists(fromParty, message.businessDomainIdentifier())) {
            throw new ConnectorProcessingModeVerificationException(
                    String.format(
                            "message fromParty [%s] is not configured on the connector! "
                            + "Check the P-Mode linked to business domain with uuid [%s]",
                            fromParty, message.businessDomainIdentifier()
                    )
            );
        }
    }

    private void processCreateVerification(ConnectorMessage message) {
        var warning = String.format(
                "message with uuid [%s] verification failed because P-Mode CREATE "
                + "verification mode is not supported!",
                message.identifier()
        );
        log.warn(warning);
        throw new ConnectorProcessingModeVerificationException(warning);
    }

    private void processServiceAndActionVerification(ConnectorMessage message) {
        log.debug("verifying service and action for message [{}]", message.identifier());

        try {
            var as4Properties = message.as4Properties();

            this.serviceService.findByNameAndBusinessDomain(
                    as4Properties.service().name(),
                    message.businessDomainIdentifier().messageLaneIdentifier()
            );

            this.actionService.findByNameAndBusinessDomain(
                    as4Properties.action().name(), message.businessDomainIdentifier()
            );
        } catch (NotFoundException e) {
            log.error("message with uuid [{}] verification failed", message.identifier());

            throw new ConnectorProcessingModeVerificationException(
                    "message verification failed", e
            );
        }
    }
}
