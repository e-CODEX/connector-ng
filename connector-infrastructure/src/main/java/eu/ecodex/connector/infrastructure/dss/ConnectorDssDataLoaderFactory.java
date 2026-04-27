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

import eu.ecodex.connector.infrastructure.property.dss.ConnectorDssProperties;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * Factory for creating {@link DSSFileLoader} instances with different caching strategies.
 *
 * <p>This component provides two main types of loaders:
 * <ul>
 *     <li>An online loader that always fetches fresh data while maintaining a cache
 *     <li>A cache-only loader that reads exclusively from the local cache
 * </ul>
 */
@Component
public class ConnectorDssDataLoaderFactory {
    private final ConnectorDssProperties dssProperties;
    private final DataLoader defaultDataLoader;
    private final File cacheDir;

    /**
     * Constructs a new factory with the given configuration.
     *
     * @param dssProperties     the DSS connector properties containing cache configuration
     * @param defaultDataLoader the default data loader used for online retrieval
     *
     * @throws IllegalStateException if the cache directory cannot be resolved or created
     */
    public ConnectorDssDataLoaderFactory(
            ConnectorDssProperties dssProperties,
            DataLoader defaultDataLoader) {
        this.dssProperties = dssProperties;
        this.defaultDataLoader = defaultDataLoader;

        this.cacheDir = resolveAndCreateCacheDir(dssProperties);
    }

    /**
     * Creates a {@link DSSFileLoader} that fetches data online and updates the cache.
     *
     * <p>The cache expiration is set to {@code 0}, meaning cached entries are always
     * considered expired and fresh data is retrieved on each request.
     *
     * @return a {@link DSSFileLoader} configured for online access with caching
     */
    public DSSFileLoader createOnlineDataLoader() {
        var loader = new FileCacheDataLoader();
        loader.setCacheExpirationTime(0);
        loader.setFileCacheDirectory(this.cacheDir);
        loader.setDataLoader(defaultDataLoader);

        return loader;
    }

    /**
     * Creates a {@link DSSFileLoader} that reads exclusively from the local cache.
     *
     * <p>The loader uses the configured cache expiration time and does not attempt
     * to fetch remote data (via {@link IgnoreDataLoader}).
     *
     * @return a {@link DSSFileLoader} configured for cache-only access
     */
    public DSSFileLoader createFileCacheDataLoader() {
        var loader = new FileCacheDataLoader();
        loader.setFileCacheDirectory(this.cacheDir);
        loader.setCacheExpirationTime(
                dssProperties.getCache().getExpirationMs().toMillis()
        );
        loader.setDataLoader(new IgnoreDataLoader());

        return loader;
    }

    private File resolveAndCreateCacheDir(ConnectorDssProperties properties) {
        try {
            var dir = properties.getCache().getLocation().getFile();
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IllegalStateException(
                        "failed to create DSS cache directory: " + dir.getAbsolutePath()
                );
            }

            return dir;
        } catch (IOException e) {
            throw new IllegalStateException("failed to resolve DSS cache directory", e);
        }
    }
}
