/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

/**
 * Utility class for opening streams from various sources.
 */
public final class ResourceStreams {
    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final Pattern URL_SCHEME = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]+:.*");

    /**
     * Opens an {@link InputStream} for the given location, which may be a {@code classpath:}
     * resource, a URL, or a filesystem path.
     *
     * @param path the resource location; must not be null or blank
     *
     * @return in an open stream the caller is responsible for closing
     *
     * @throws IOException           if the resource cannot be opened
     * @throws IllegalStateException if the resource does not exist
     */
    public static InputStream openStream(String path) throws IOException {
        if (!StringUtils.hasText(path)) {
            throw new IllegalArgumentException("Resource path must not be blank");
        }

        if (path.startsWith(CLASSPATH_PREFIX)) {
            var resource = new ClassPathResource(path.substring(CLASSPATH_PREFIX.length()));
            if (!resource.exists()) {
                throw new IllegalStateException("Classpath resource not found: " + path);
            }
            return resource.getInputStream();
        }

        if (URL_SCHEME.matcher(path).matches()) {
            return URI.create(path).toURL().openStream();
        }

        var file = Path.of(path);
        if (!Files.exists(file)) {
            throw new IllegalStateException("Resource not found: " + path);
        }

        return Files.newInputStream(file);
    }
}
