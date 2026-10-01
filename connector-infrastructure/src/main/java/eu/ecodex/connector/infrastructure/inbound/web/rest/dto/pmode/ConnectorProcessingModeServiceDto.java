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

import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import java.io.Serializable;
import lombok.Builder;

/**
 * Represents a Data Transfer Object (DTO) for the processing mode service.
 *
 * @param name   The name of the service
 * @param type   The type of the service
 * @param isTest whether the current service is for testing or not
 */
@Builder
public record ConnectorProcessingModeServiceDto(
    String name,
    String type,
    boolean isTest
) implements Serializable {
    /**
     * Creates a DTO from a connector service and determines whether it matches the configured test
     * message service.
     *
     * @param service         the connector service to convert
     * @param testServiceName the configured name of the test message service
     * @param testServiceType the configured type of the test message service
     *
     * @return a DTO representing the connector service
     */
    public static ConnectorProcessingModeServiceDto from(
        ConnectorService service,
        String testServiceName,
        String testServiceType) {
        var name = service.name();
        var type = service.type();
        return ConnectorProcessingModeServiceDto
            .builder()
            .name(name)
            .type(type)
            .isTest(name.equals(testServiceName) && type.equals(testServiceType))
            .build();
    }
}
