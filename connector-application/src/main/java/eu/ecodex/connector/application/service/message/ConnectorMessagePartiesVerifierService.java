/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import eu.ecodex.connector.application.exception.ConnectorMessagePartyException;
import eu.ecodex.connector.application.port.api.message.ConnectorMessagePartiesVerifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link ConnectorMessagePartiesVerifier} service.
 */
@Slf4j
@Component
public class ConnectorMessagePartiesVerifierService implements ConnectorMessagePartiesVerifier {
    @Override
    public void verify(@NonNull ConnectorMessage message) {
        log.debug("Checking message [{}] parties info", message.identifier());

        final var as4Properties = message.as4Properties();
        final var fromParty = as4Properties.fromParty();
        final var toParty = as4Properties.toParty();

        if (fromParty == null || toParty == null) {
            throw new ConnectorMessagePartyException("message must have 'from' and 'to' parties");
        }

        var direction = message.direction();

        if (direction == null) {
            throw new IllegalStateException("message must have a direction");
        }

        switch (direction) {
            case BACKEND_TO_GATEWAY -> {
                if (fromParty.roleType() != ConnectorPartyRoleType.INITIATOR) {
                    throw new ConnectorMessagePartyException(
                        "message 'fromParty' roleType must be INITIATOR but was "
                            + fromParty.roleType()
                    );
                }

                if (toParty.roleType() != ConnectorPartyRoleType.RESPONDER) {
                    throw new ConnectorMessagePartyException(
                        "message 'toParty' roleType must be RESPONDER but was "
                            + toParty.roleType()
                    );
                }
            }
            case GATEWAY_TO_BACKEND ->
                throw new UnsupportedOperationException("not implemented yet");
            default -> throw new AssertionError("unreachable: unexpected direction: " + direction);
        }
    }
}
