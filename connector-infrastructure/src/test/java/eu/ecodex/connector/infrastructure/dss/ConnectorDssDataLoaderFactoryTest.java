/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.dss;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("ConnectorDssDataLoaderFactory")
public class ConnectorDssDataLoaderFactoryTest extends BaseDssTest {
    @Autowired
    private ConnectorDssDataLoaderFactory dataLoaderFactory;

    @Test
    void should_create_online_data_loader() {
        var onlineDataLoader = dataLoaderFactory.createOnlineDataLoader();
        assertThat(onlineDataLoader).isNotNull();
    }

    @Test
    void should_create_file_cache_data_loader() {
        var fileCacheDataLoader = dataLoaderFactory.createFileCacheDataLoader();
        assertThat(fileCacheDataLoader).isNotNull();
    }
}
