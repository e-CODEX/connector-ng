/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository;

import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.domain.model.link.ConnectorLinkMode;
import eu.ecodex.connector.domain.model.link.ConnectorLinkType;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.spi.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.infrastructure.property.link.ConnectorLinkProperties;
import eu.ecodex.connector.infrastructure.property.link.LinkPartnerProperties;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link ConnectorLinkPartnerRepository}.
 */
@Slf4j
@Component
public class ConnectorLinkPartnerRepositoryImpl implements ConnectorLinkPartnerRepository {
    private final Map<ConnectorLinkPartnerName, ConnectorLinkPartner> partners;

    /**
     * Constructs an instance of {@code ConnectorLinkPartnerRepositoryImpl} by initializing a
     * registry of link partners from the provided {@link ConnectorLinkProperties}.
     *
     * <p>The registry is a read-only map of {@link ConnectorLinkPartnerName} to
     * {@link ConnectorLinkPartner}, populated based on the configuration details from both gateway
     * and backend components.
     *
     * @param properties The {@link ConnectorLinkProperties} containing gateway and backend link
     *                   partner configurations required to populate the registry.
     */
    public ConnectorLinkPartnerRepositoryImpl(ConnectorLinkProperties properties) {
        log.info("initializing link partner registry");

        Map<ConnectorLinkPartnerName, ConnectorLinkPartner> map = new HashMap<>();

        // gateway partners
        if (properties.getGateway() != null) {
            properties.getGateway().getLinkPartners()
                      .forEach(partnerProperties -> {
                          var linkPartnerName = ConnectorLinkPartnerName
                                  .builder()
                                  .name(partnerProperties.getName())
                                  .build();

                          map.put(
                                  linkPartnerName,
                                  toDomain(partnerProperties, ConnectorLinkType.GATEWAY)
                          );
                      });
        }

        // backend partners
        properties.getBackend().stream()
                  .flatMap(b -> b.getLinkPartners().stream())
                  .forEach(partnerProperties -> {
                      var linkPartnerName = ConnectorLinkPartnerName
                              .builder()
                              .name(partnerProperties.getName())
                              .build();

                      map.put(
                              linkPartnerName,
                              toDomain(partnerProperties, ConnectorLinkType.BACKEND)
                      );
                  });

        this.partners = Collections.unmodifiableMap(map);
    }

    @Override
    public ConnectorLinkPartner findByName(@NonNull ConnectorLinkPartnerName name) {
        return this.partners.getOrDefault(name, null);
    }

    @Override
    public ConnectorLinkPartner findByCertificateDn(@NonNull String certificateDn) {
        return this.partners.values().stream()
                            .filter(partner -> Objects.equals(
                                    partner.certificateDn(), certificateDn))
                            .findFirst()
                            .orElse(null);
    }

    public List<ConnectorLinkPartner> findAll() {
        return partners.values().stream().toList();
    }

    private ConnectorLinkPartner toDomain(
            LinkPartnerProperties properties, ConnectorLinkType type) {
        var linkPartnerName = ConnectorLinkPartnerName
                .builder()
                .name(properties.getName())
                .build();

        var linkPartnerBuilder = ConnectorLinkPartner
                .builder()
                .name(linkPartnerName)
                .description(properties.getDescription())
                .enabled(properties.isEnabled())
                .type(type)
                .source(ConnectorConfigurationSource.IMPLEMENTATION)
                .receiverMode(ConnectorLinkMode.valueOf(properties.getReceiverMode().toUpperCase()))
                .senderMode(ConnectorLinkMode.valueOf(properties.getSenderMode().toUpperCase()));

        if (type == ConnectorLinkType.BACKEND) {
            linkPartnerBuilder
                    .encryptionAlias(properties.getProperties().getEncryptionAlias())
                    .certificateDn(properties.getProperties().getCertificateDn());
        }

        return linkPartnerBuilder.build();
    }

    @PostConstruct
    void init() {
        this.partners
                .forEach((name, partner) ->
                                 log.info("registered link partner: {} - {}", name, partner)
                );
    }
}
