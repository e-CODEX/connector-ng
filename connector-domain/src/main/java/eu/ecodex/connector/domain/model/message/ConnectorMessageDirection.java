/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message;

import static eu.ecodex.connector.domain.model.message.ConnectorMessageDirectionType.BACKEND;
import static eu.ecodex.connector.domain.model.message.ConnectorMessageDirectionType.GATEWAY;

import jakarta.annotation.Nonnull;
import java.io.Serializable;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Represents the direction of message flow between the backend system and the gateway.
 *
 * <p>This enum is used to describe the source and target of a message as it transitions
 * within the system. It captures two possible flows:
 * <ul>
 *     <li>
 *         BACKEND_TO_GATEWAY: Indicates that the message originates from the backend system and
 *         is directed toward the gateway.
 *     </li>
 *     <li>
 *         GATEWAY_TO_BACKEND: Indicates that the message originates from the gateway and is
 *         directed toward the backend system.
 *     </li>
 * </ul>
 *
 * <p>Each direction is characterized by a source and a target
 * of type {@link ConnectorMessageDirectionType}.
 */
@Getter
public enum ConnectorMessageDirection implements Serializable {
    BACKEND_TO_GATEWAY(BACKEND, GATEWAY),
    GATEWAY_TO_BACKEND(GATEWAY, BACKEND);

    private final ConnectorMessageDirectionType source;
    private final ConnectorMessageDirectionType target;

    ConnectorMessageDirection(
            ConnectorMessageDirectionType source, ConnectorMessageDirectionType target) {
        this.source = source;
        this.target = target;
    }

    /**
     * Retrieves a {@link ConnectorMessageDirection} that matches the provided source and target
     * {@link ConnectorMessageDirectionType}.
     *
     * @param source the source of the message direction must not be null
     * @param target the target of the message direction must not be null
     *
     * @return the matching {@link ConnectorMessageDirection} for the specified source and target
     * @throws java.util.NoSuchElementException if no matching {@link ConnectorMessageDirection} is
     *                                          found
     */
    public static ConnectorMessageDirection from(
            @Nonnull ConnectorMessageDirectionType source,
            @Nonnull ConnectorMessageDirectionType target) {
        return Stream.of(ConnectorMessageDirection.values())
                     .filter(direction -> direction.source.equals(source)
                                          && direction.target.equals(target)
                     )
                     .findFirst()
                     .orElse(null);
    }

    public static ConnectorMessageDirection revert(ConnectorMessageDirection direction) {
        return from(direction.getTarget(), direction.source);
    }
}
