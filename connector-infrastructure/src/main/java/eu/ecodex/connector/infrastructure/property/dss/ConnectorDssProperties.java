/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property.dss;

import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Root configuration properties for DSS (Digital Signature Services) integration.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "connector.dss")
public class ConnectorDssProperties {
    @NotNull
    private DssCacheProperties cache;
    private DssProxyProperties proxy;
    private Map<String, DssTrustListSourceGroupProperties> trustListSources = new LinkedHashMap<>();
    private Map<String, DssTimestampServerProperties> timeStampServers = new LinkedHashMap<>();
}
