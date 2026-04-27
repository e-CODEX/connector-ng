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

import eu.ecodex.connector.infrastructure.property.common.KeystoreProperties;
import eu.ecodex.connector.infrastructure.property.common.PrivateKeyProperties;
import eu.europa.esig.dss.token.AbstractKeyStoreTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

/**
 * Provides access to a configured DSS signing token backed by a keystore.
 *
 * <p>This component is responsible for:
 * <ul>
 *     <li>Loading a keystore (PKCS12 or JKS) from a configured location</li>
 *     <li>Initializing a {@link AbstractKeyStoreTokenConnection}</li>
 *     <li>Providing access to a signing key via alias</li>
 * </ul>
 *
 * <p>The keystore can be loaded from:
 * <ul>
 *     <li>Classpath resources (prefix: {@code classpath:})</li>
 *     <li>File system paths</li>
 *     <li>URLs</li>
 * </ul>
 */
@Slf4j
@Getter
public class ConnectorDssSigningTokenProvider implements Closeable {
    private static final String CLASSPATH_PREFIX = "classpath:";

    protected final KeystoreProperties keystore;
    protected final PrivateKeyProperties privateKey;
    protected final AbstractKeyStoreTokenConnection signingToken;

    /**
     * Constructs a new instance of the {@code ConnectorDssSigningTokenProvider} class.
     *
     * @param keystore   the configuration properties for accessing the signing keystore, such as
     *                   its path, password, and type
     * @param privateKey the configuration properties for accessing the private key within the
     *                   keystore, including the alias and password
     */
    public ConnectorDssSigningTokenProvider(
            KeystoreProperties keystore,
            PrivateKeyProperties privateKey) {
        this.keystore = keystore;
        this.signingToken = initSigningToken(keystore);
        this.privateKey = privateKey;
    }

    private AbstractKeyStoreTokenConnection initSigningToken(KeystoreProperties keystore) {
        var path = keystore.getPath();

        if (!StringUtils.hasText(path)) {
            throw new IllegalStateException("signing keystore path must not be blank");
        }

        log.info("loading signing keystore from: {}", keystore.getPath());

        var protection = new KeyStore.PasswordProtection(
                keystore.getPassword().toCharArray()
        );

        var token = switch (keystore.getType()) {
            case PKCS12 -> loadPkcs12(keystore, protection);
            case JKS -> loadJks(keystore, protection);
        };

        log.info("signing token loaded — {} key(s) available", token.getKeys().size());

        return token;
    }

    /**
     * Retrieves the configured signing key using the alias defined in properties.
     *
     * @return the {@link DSSPrivateKeyEntry} associated with the configured alias
     */
    public DSSPrivateKeyEntry getSigningKey() {
        if (privateKey == null) {
            throw new IllegalArgumentException("missing private key configuration");
        }

        var alias = privateKey.getAlias();
        var key = signingToken.getKey(alias);

        if (key == null) {
            throw new IllegalArgumentException(
                    "no signing key found for alias [" + alias + "] in keystore"
            );
        }

        return key;
    }

    private Pkcs12SignatureToken loadPkcs12(
            KeystoreProperties properties,
            KeyStore.PasswordProtection protection) {
        try {
            return new Pkcs12SignatureToken(resolveStream(properties.getPath()), protection);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "failed to load PKCS12 keystore: " + properties.getPath(), e
            );
        }
    }

    private JKSSignatureToken loadJks(
            KeystoreProperties properties,
            KeyStore.PasswordProtection protection) {
        try {
            return new JKSSignatureToken(resolveStream(properties.getPath()), protection);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "failed to load JKS keystore: " + properties.getPath(), e);
        }
    }

    private InputStream resolveStream(String path) throws IOException {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("keystore path must not be blank");
        }

        if (path.startsWith(CLASSPATH_PREFIX)) {
            var resourcePath = path.substring(CLASSPATH_PREFIX.length());
            var resource = new ClassPathResource(resourcePath);

            if (!resource.exists()) {
                throw new IllegalStateException("classpath resource not found: " + resource);
            }

            return resource.getInputStream();
        }

        try {
            return URI.create(path).toURL().openStream();
        } catch (Exception e) {
            return Files.newInputStream(Path.of(path));
        }
    }

    @Override
    public void close() {
        this.signingToken.close();
    }
}
