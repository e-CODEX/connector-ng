/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.pmode;

import eu.ecodex.connector.application.service.usecase.pmode.ConnectorRegisterProcessingMode;
import eu.ecodex.connector.application.util.SecureXmlParserUtil;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeException;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.keystore.ConnectorKeystore;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.domain.spi.ConnectorActionRepository;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.domain.spi.ConnectorKeystoreRepository;
import eu.ecodex.connector.domain.spi.ConnectorPartyRepository;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.spi.ConnectorServiceRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Implementation of the {@link ConnectorRegisterProcessingMode} service.
 */
@Slf4j
@Service
@Transactional
public class ConnectorRegisterProcessingModeService implements ConnectorRegisterProcessingMode {
    private final ConnectorProcessingModeRepository processingModeRepository;
    private final ConnectorBusinessDomainRepository businessDomainRepository;
    private final ConnectorKeystoreRepository keystoreRepository;
    private final ConnectorPartyRepository partyRepository;
    private final ConnectorActionRepository actionRepository;
    private final ConnectorServiceRepository serviceRepository;

    /**
     * Creates an instance of {@code ConnectorRegisterProcessingModeService}.
     *
     * @param processingModeRepository The repository used for managing
     *                                 {@link ConnectorProcessingMode} entities.
     * @param businessDomainRepository The repository used for managing
     *                                 {@link ConnectorBusinessDomain} entities.
     * @param keystoreRepository       The repository used for managing {@link ConnectorKeystore}
     *                                 entities.
     * @param partyRepository          The repository used for managing {@link ConnectorParty}
     *                                 entities.
     * @param actionRepository         The repository used for managing {@link ConnectorAction}
     *                                 entities.
     * @param serviceRepository        The repository used for managing {@link ConnectorService}
     *                                 entities.
     */
    public ConnectorRegisterProcessingModeService(
            ConnectorProcessingModeRepository processingModeRepository,
            ConnectorBusinessDomainRepository businessDomainRepository,
            ConnectorKeystoreRepository keystoreRepository,
            ConnectorPartyRepository partyRepository, ConnectorActionRepository actionRepository,
            ConnectorServiceRepository serviceRepository) {
        this.processingModeRepository = processingModeRepository;
        this.businessDomainRepository = businessDomainRepository;
        this.keystoreRepository = keystoreRepository;
        this.partyRepository = partyRepository;
        this.actionRepository = actionRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public ConnectorProcessingMode execute(
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
            @NonNull ConnectorProcessingMode processingMode) {
        log.debug(
                "creating new processing mode [{}] for business domain [{}]: ", processingMode,
                businessDomainIdentifier
        );

        var foundBusinessDomain = this.businessDomainRepository.findByIdentifier(
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

        var createdTruststore = this.keystoreRepository.save(
                Objects.requireNonNull(parsedProcessingMode.truststore()),
                businessDomainIdentifier
        );

        createdProcessingMode = createdProcessingMode
                .toBuilder()
                .truststore(createdTruststore)
                .build();

        this.partyRepository.saveAll(
                Objects.requireNonNull(parsedProcessingMode.parties()).stream().toList(),
                businessDomainIdentifier
        );
        this.serviceRepository.saveAll(
                Objects.requireNonNull(parsedProcessingMode.services()).stream().toList(),
                businessDomainIdentifier
        );
        this.actionRepository.saveAll(
                Objects.requireNonNull(parsedProcessingMode.actions()).stream().toList(),
                businessDomainIdentifier
        );

        var updatedProcessingMode = this.processingModeRepository.updateKeystore(
                createdProcessingMode.uuid(), createdTruststore.uuid()
        );

        log.debug("processing mode [{}] created successfully", createdProcessingMode);

        return updatedProcessingMode;
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
}
