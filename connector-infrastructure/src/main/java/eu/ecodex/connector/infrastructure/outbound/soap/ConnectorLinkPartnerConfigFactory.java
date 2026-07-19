/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.soap;

import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.infrastructure.property.link.ConnectorLinkProperties;
import eu.ecodex.connector.infrastructure.property.link.LinkConfigProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating and managing configurations of connector link partners.
 */
@Slf4j
@Component
public class ConnectorLinkPartnerConfigFactory {
    private final Map<ConnectorLinkPartnerName, LinkConfigProperties> partnersConfig;

    /**
     * Constructs an instance of ConnectorLinkPartnerConfigFactory and initializes the link partner
     * registry.
     *
     * <p>This method processes the provided configuration properties and registers backend link
     * partners with their corresponding configurations in an immutable map. If duplicate partner
     * names are detected, an exception is thrown.
     *
     * @param properties Configuration properties containing the list of backend components and
     *                   their respective link partner and configuration details.
     */
    public ConnectorLinkPartnerConfigFactory(ConnectorLinkProperties properties) {
        log.info("Initializing link partner registry");

        var partnersConfigMap = new HashMap<ConnectorLinkPartnerName, LinkConfigProperties>();

        // backend partners
        var backendList = properties.getBackend();
        if (backendList != null) {
            for (var backendProperties : backendList) {
                for (var partnerProperties : backendProperties.getLinkPartners()) {
                    var linkPartnerName = ConnectorLinkPartnerName.builder()
                                                                  .name(partnerProperties.getName())
                                                                  .build();
                    checkNoDuplicateName(partnersConfigMap, linkPartnerName);
                    partnersConfigMap.put(linkPartnerName, backendProperties.getLinkConfig());
                }
            }
        }

        this.partnersConfig = Collections.unmodifiableMap(partnersConfigMap);
    }

    /**
     * Retrieves the configuration properties associated with a specific link partner name.
     *
     * @param name The name of the link partner used to look up its configuration properties. Must
     *             not be null.
     *
     * @return The {@code LinkConfigProperties} associated with the specified link partner name.
     *     Returns {@code null} if no configuration is found for the given name.
     */
    public LinkConfigProperties findByLinkPartnerName(@NonNull ConnectorLinkPartnerName name) {
        return this.partnersConfig.getOrDefault(name, null);
    }

    private void checkNoDuplicateName(
        Map<ConnectorLinkPartnerName, ?> map,
        ConnectorLinkPartnerName name) {
        if (map.containsKey(name)) {
            throw new IllegalStateException(
                "Duplicate link partner name detected during registry initialisation: '"
                    + name.name() + "'. Each partner name must be unique across gateway "
                    + "and backend."
            );
        }
    }
}
