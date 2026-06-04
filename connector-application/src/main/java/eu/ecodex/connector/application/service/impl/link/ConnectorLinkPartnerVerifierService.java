/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.link;

import eu.ecodex.connector.application.service.usecase.link.ConnectorLinkPartnerVerifier;
import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerSubmissionException;
import eu.ecodex.connector.domain.model.link.ConnectorLinkType;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.spi.link.ConnectorLinkPartnerRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorLinkPartnerVerifier} service.
 */
@Slf4j
@Service
public class ConnectorLinkPartnerVerifierService implements ConnectorLinkPartnerVerifier {
    private final ConnectorLinkPartnerRepository linkPartnerRepository;

    /**
     * Constructs an instance of {@code ConnectorGatewayLinkEventHandler}.
     *
     * @param linkPartnerRepository the repository used to retrieve {@code ConnectorLinkPartner}
     *                              instances based on their names. This dependency is used to look
     *                              up the link partner associated with a given gateway name.
     */
    public ConnectorLinkPartnerVerifierService(
            ConnectorLinkPartnerRepository linkPartnerRepository) {
        this.linkPartnerRepository = linkPartnerRepository;
    }

    @Override
    public void verify(@NonNull ConnectorMessage message) {
        log.debug("Verifying the message [{}] link partner", message.identifier());
        var linkPartnerName = getLinkPartnerName(message);

        var linkPartner = this.linkPartnerRepository.findByName(linkPartnerName);

        if (linkPartner == null) {
            throw new ConnectorLinkPartnerSubmissionException(
                    String.format(
                            "The LinkPartner with name [%s] could not be found!",
                            linkPartnerName
                    ));
        }

        validateDirection(message.direction(), linkPartner.type());

        log.debug(
                "Gateway message [{}] processed for the link partner [{}]",
                message.identifier(), linkPartner
        );
    }

    private ConnectorLinkPartnerName getLinkPartnerName(ConnectorMessage message) {
        if (message.direction() == ConnectorMessageDirection.GATEWAY_TO_BACKEND) {
            return new ConnectorLinkPartnerName(message.backendName());
        }

        return new ConnectorLinkPartnerName(message.gatewayName());
    }

    private void validateDirection(ConnectorMessageDirection direction, ConnectorLinkType type) {
        boolean invalid = (
                direction == ConnectorMessageDirection.BACKEND_TO_GATEWAY
                        && type != ConnectorLinkType.GATEWAY)
                || (
                direction == ConnectorMessageDirection.GATEWAY_TO_BACKEND
                        && type != ConnectorLinkType.BACKEND);

        if (invalid) {
            throw new ConnectorLinkPartnerSubmissionException(
                    "The link partner does not match the message direction"
            );
        }
    }
}
