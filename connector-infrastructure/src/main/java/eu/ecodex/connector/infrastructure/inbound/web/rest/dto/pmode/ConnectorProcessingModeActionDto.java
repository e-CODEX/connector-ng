/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode;

import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import java.io.Serializable;
import lombok.Builder;

/**
 * Represents a Data Transfer Object (DTO) for the processing mode action.
 *
 * @param name   The name of the action.
 * @param isTest whether the current service is for testing or not
 */
@Builder
public record ConnectorProcessingModeActionDto(
    String name,
    boolean isTest
) implements Serializable {
    /**
     * Creates a DTO from a connector action and determines whether it matches the configured test
     * message action.
     *
     * @param action         the connector action to convert
     * @param testActionName the configured name of the test message action
     *
     * @return a DTO representing the connector action
     */
    public static ConnectorProcessingModeActionDto from(
        ConnectorAction action,
        String testActionName) {
        var name = action.name();
        return ConnectorProcessingModeActionDto.builder()
                                               .name(name)
                                               .isTest(name.equals(testActionName))
                                               .build();
    }
}
