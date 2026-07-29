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
import eu.ecodex.connector.infrastructure.util.ResourceStreams;
import eu.europa.esig.dss.token.AbstractKeyStoreTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import java.io.Closeable;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
    private final KeystoreProperties keystore;
    private final PrivateKeyProperties privateKey;
    private final AbstractKeyStoreTokenConnection signingToken;

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
        this.keystore = Objects.requireNonNull(keystore, "Keystore properties must not be null");
        this.privateKey =
            Objects.requireNonNull(privateKey, "Private key properties must not be null");

        if (!StringUtils.hasText(keystore.getPath())) {
            throw new IllegalStateException("Signing keystore path must not be blank");
        }

        if (keystore.getPassword() == null) {
            throw new IllegalStateException("Signing keystore password must not be null");
        }

        if (!StringUtils.hasText(privateKey.getAlias())) {
            throw new IllegalStateException("Signing key alias must not be blank");
        }

        this.signingToken = initSigningToken(keystore);
    }

    private AbstractKeyStoreTokenConnection initSigningToken(KeystoreProperties properties) {
        var path = properties.getPath();
        log.debug("Loading signing keystore from: {}", path);

        var protection = new KeyStore.PasswordProtection(properties.getPassword().toCharArray());
        var token = switch (properties.getType()) {
            case PKCS12 -> loadPkcs12(path, protection);
            case JKS -> loadJks(path, protection);
        };

        if (log.isDebugEnabled()) {
            log.debug("Signing token loaded — {} key(s) available", token.getKeys().size());
        }

        return token;
    }

    /**
     * Retrieves the configured signing key using the alias defined in properties.
     *
     * @return the {@link DSSPrivateKeyEntry} associated with the configured alias
     *
     * @throws IllegalStateException if no key exists for the configured alias
     */
    public DSSPrivateKeyEntry getSigningKey() {
        var alias = privateKey.getAlias();
        var key = signingToken.getKey(alias);
        if (key == null) {
            throw new IllegalStateException(
                "No signing key found for alias [" + alias + "] in keystore");
        }
        return key;
    }

    private Pkcs12SignatureToken loadPkcs12(String path, KeyStore.PasswordProtection protection) {
        try (var is = ResourceStreams.openStream(path)) {
            return new Pkcs12SignatureToken(is, protection);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load PKCS12 keystore: " + path, e);
        }
    }

    private JKSSignatureToken loadJks(String path, KeyStore.PasswordProtection protection) {
        try (var is = ResourceStreams.openStream(path)) {
            return new JKSSignatureToken(is, protection);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load JKS keystore: " + path, e);
        }
    }

    @Override
    public void close() {
        signingToken.close();
    }
}
