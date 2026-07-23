/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.adapter.pmode;

import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeParser;
import eu.ecodex.connector.infrastructure.outbound.adapter.exception.ConnectorProcessingModeParsingException;
import eu.ecodex.connector.infrastructure.util.SecureXmlParserUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * DOM-based implementation of {@link ConnectorProcessingModeParser}.
 *
 * <p>Extracts the parties, services, and actions declared in a Domibus processing mode
 * (PMode) XML definition. Parsing is purely technical: no persistence and no knowledge of the
 * business domain the processing mode will be attached to.
 */
@Slf4j
@Component
public class ConnectorDomProcessingModeParser implements ConnectorProcessingModeParser {
    private static final String ATTRIBUTE_PARTY = "party";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final String ATTRIBUTE_TYPE = "type";
    private static final String ATTRIBUTE_PARTY_ID = "partyId";
    private static final String ATTRIBUTE_PARTY_ID_TYPE = "partyIdType";

    private static final String ELEMENT_PARTY = "party";
    private static final String ELEMENT_PARTY_ID_TYPE = "partyIdType";
    private static final String ELEMENT_IDENTIFIER = "identifier";
    private static final String ELEMENT_SERVICE = "service";
    private static final String ELEMENT_ACTION = "action";

    /**
     * Role assigned to every party declared in a PMode. The connector only ever acts as a gateway,
     * so the value is fixed rather than read from the definition.
     */
    private static final String GATEWAY_ROLE = "GW";

    private static Iterable<Element> elementsOf(NodeList nodes) {
        var elements = new java.util.ArrayList<Element>(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element element) {
                elements.add(element);
            }
        }

        return elements;
    }

    private static java.util.Optional<Element> firstChildElement(Element parent) {
        var children = parent.getElementsByTagName(ELEMENT_IDENTIFIER);

        for (int i = 0; i < children.getLength(); i++) {
            var child = children.item(i);
            if (child instanceof Element element && element.getParentNode() == parent) {
                return java.util.Optional.of(element);
            }
        }

        return java.util.Optional.empty();
    }

    private static String requiredAttribute(Element element, String attribute, String context) {
        var value = element.getAttribute(attribute);

        if (!StringUtils.hasText(value)) {
            throw new ConnectorProcessingModeParsingException(
                "Missing required attribute [%s] on %s".formatted(attribute, context));
        }

        return value.trim();
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    @Override
    public ParsedProcessingMode parse(byte[] content) {
        if (content == null || content.length == 0) {
            throw new ConnectorProcessingModeParsingException(
                "The processing mode definition is empty");
        }

        var root = parseDocumentRoot(content);
        var homePartyName = requiredAttribute(
            root,
            ATTRIBUTE_PARTY,
            "processing mode root element"
        );

        var partyIdTypes = retrievePartyIdTypes(root.getElementsByTagName(ELEMENT_PARTY_ID_TYPE));
        var parties = retrieveParties(
            root.getElementsByTagName(ELEMENT_PARTY), partyIdTypes, homePartyName);
        var services = retrieveServices(root.getElementsByTagName(ELEMENT_SERVICE));
        var actions = retrieveActions(root.getElementsByTagName(ELEMENT_ACTION));

        if (parties.stream().noneMatch(party -> homePartyName.equals(party.name()))) {
            throw new ConnectorProcessingModeParsingException(
                "Home party [%s] declared on the root element is not defined in the parties list"
                    .formatted(homePartyName));
        }

        log.debug(
            "Processing mode parsed: {} parties, {} services, {} actions",
            parties.size(), services.size(), actions.size()
        );

        return new ParsedProcessingMode(homePartyName, parties, services, actions);
    }

    private Element parseDocumentRoot(byte[] content) {
        try {
            var document = SecureXmlParserUtil.parseSecurely(new String(content));
            document.getDocumentElement().normalize();
            return document.getDocumentElement();
        } catch (Exception e) {
            throw new ConnectorProcessingModeParsingException(
                "The processing mode file is not a well-formed XML document", e);
        }
    }

    private Map<String, String> retrievePartyIdTypes(NodeList partyIdTypeNodes) {
        var partyIdTypes = new HashMap<String, String>();

        for (var partyIdType : elementsOf(partyIdTypeNodes)) {
            var name = requiredAttribute(partyIdType, ATTRIBUTE_NAME, "partyIdType element");
            var value = requiredAttribute(
                partyIdType, ATTRIBUTE_VALUE, "partyIdType [%s]".formatted(name));

            var previous = partyIdTypes.put(name, value);
            if (previous != null && !previous.equals(value)) {
                throw new ConnectorProcessingModeParsingException(
                    "Duplicate partyIdType [%s] with conflicting values".formatted(name));
            }
        }

        return partyIdTypes;
    }

    private Set<ConnectorParty> retrieveParties(
        NodeList partyNodes, Map<String, String> partyIdTypes, String homePartyName) {

        var parties = new HashSet<ConnectorParty>();

        for (var party : elementsOf(partyNodes)) {
            var name = requiredAttribute(party, ATTRIBUTE_NAME, "party element");

            var identifier = firstChildElement(party)
                .orElseThrow(() -> new ConnectorProcessingModeParsingException(
                    "Party [%s] has no <identifier> element".formatted(name)));

            var partyId = requiredAttribute(
                identifier, ATTRIBUTE_PARTY_ID, "identifier of party [%s]".formatted(name));
            var partyIdTypeName = requiredAttribute(
                identifier, ATTRIBUTE_PARTY_ID_TYPE, "identifier of party [%s]".formatted(name));

            var partyIdTypeValue = partyIdTypes.get(partyIdTypeName);
            if (partyIdTypeValue == null) {
                throw new ConnectorProcessingModeParsingException(
                    "Party [%s] references the undeclared partyIdType [%s]"
                        .formatted(name, partyIdTypeName));
            }

            // A party is usable in both directions, so one entry per role type is created.
            for (var roleType : ConnectorPartyRoleType.values()) {
                parties.add(
                    ConnectorParty.builder()
                                  .name(name)
                                  .identifier(partyId)
                                  .identifierType(partyIdTypeValue)
                                  .role(GATEWAY_ROLE)
                                  .roleType(roleType)
                                  .isHome(homePartyName.equals(name))
                                  .build());
            }
        }

        if (parties.isEmpty()) {
            throw new ConnectorProcessingModeParsingException(
                "The processing mode declares no party");
        }

        return parties;
    }

    private Set<ConnectorService> retrieveServices(NodeList serviceNodes) {
        var services = new HashSet<ConnectorService>();

        for (var service : elementsOf(serviceNodes)) {
            var value = requiredAttribute(service, ATTRIBUTE_VALUE, "service element");

            services.add(
                ConnectorService.builder()
                                .name(value)
                                .type(emptyToNull(service.getAttribute(ATTRIBUTE_TYPE)))
                                .build());
        }

        if (services.isEmpty()) {
            throw new ConnectorProcessingModeParsingException(
                "The processing mode declares no service");
        }

        return services;
    }

    private Set<ConnectorAction> retrieveActions(NodeList actionNodes) {
        var actions = new HashSet<ConnectorAction>();

        for (var action : elementsOf(actionNodes)) {
            var value = requiredAttribute(action, ATTRIBUTE_VALUE, "action element");
            actions.add(ConnectorAction.builder().name(value).build());
        }

        if (actions.isEmpty()) {
            throw new ConnectorProcessingModeParsingException(
                "The processing mode declares no action");
        }

        return actions;
    }
}
