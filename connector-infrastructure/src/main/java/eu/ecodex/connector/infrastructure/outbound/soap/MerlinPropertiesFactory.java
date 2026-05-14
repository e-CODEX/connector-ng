package eu.ecodex.connector.infrastructure.outbound.soap;

import eu.ecodex.connector.infrastructure.property.link.LinkEndpointProperties;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory class responsible for creating and configuring a {@code Properties} object for Apache
 * WSS4J's Merlin Crypto implementation, using the provided link endpoint properties. This class
 * simplifies the process of setting up cryptographic properties required for securing SOAP
 * communication.
 */
@Slf4j
@Component
public class MerlinPropertiesFactory {
    private static final String PROVIDER = "org.apache.wss4j.crypto.provider";
    private static final String KS_TYPE = "org.apache.wss4j.crypto.merlin.keystore.type";
    private static final String KS_PASSWORD = "org.apache.wss4j.crypto.merlin.keystore.password";
    private static final String KS_FILE = "org.apache.wss4j.crypto.merlin.keystore.file";
    private static final String KS_ALIAS = "org.apache.wss4j.crypto.merlin.keystore.alias";
    private static final String KS_KEY_PASSWORD
            = "org.apache.wss4j.crypto.merlin.keystore.private.password";
    private static final String TS_TYPE = "org.apache.wss4j.crypto.merlin.truststore.type";
    private static final String TS_PASSWORD = "org.apache.wss4j.crypto.merlin.truststore.password";
    private static final String TS_FILE = "org.apache.wss4j.crypto.merlin.truststore.file";
    private static final String LOAD_CACERTS = "org.apache.wss4j.crypto.merlin.load.cacerts";
    // private static final String ENCRYPT_USERNAME = "security.encryption.username";

    /**
     * Creates and configures a set of properties required for Apache WSS4J's Merlin Crypto
     * implementation based on the provided link endpoint properties.
     *
     * @param linkEndpointProperties the properties containing keystore and private-key
     *                               configuration. Must not be null, and the keystore and private
     *                               key must be configured with valid values for type, path, alias,
     *                               and passwords.
     *
     * @return a {@code Properties} object populated with the necessary signing configurations for
     *         the Merlin Crypto implementation.
     *
     * @throws IllegalArgumentException if {@code linkEndpointProperties}, its keystore, or its
     *                                  private key is null.
     */
    public Properties createSigningProperties(LinkEndpointProperties linkEndpointProperties) {
        if (linkEndpointProperties == null) {
            throw new IllegalArgumentException("Link endpoint properties are not set");
        }

        var keystore = linkEndpointProperties.getKeystore();

        if (keystore == null) {
            throw new IllegalArgumentException("Keystore must not be null");
        }

        var privateKey = linkEndpointProperties.getPrivateKey();

        if (privateKey == null) {
            throw new IllegalArgumentException("Private key must not be null");
        }

        var properties = new Properties();

        properties.setProperty(PROVIDER, "org.apache.wss4j.common.crypto.Merlin");

        properties.setProperty(KS_TYPE, keystore.getType().name());
        properties.setProperty(KS_FILE, keystore.getPath());
        properties.setProperty(KS_PASSWORD, keystore.getPassword());
        properties.setProperty(KS_ALIAS, privateKey.getAlias());
        properties.setProperty(KS_KEY_PASSWORD, privateKey.getPassword());

        return properties;
    }

    /**
     * Creates and configures a set of properties required for Apache WSS4J's Merlin Crypto
     * implementation based on the provided link endpoint properties.
     *
     * @param linkEndpointProperties the properties containing truststore configuration for
     *                               encryption. Must not be null, and the truststore must be
     *                               configured with valid values for type, path, and password.
     *
     * @return a {@code Properties} object populated with the necessary encryption configurations
     *         for the Merlin Crypto implementation.
     *
     * @throws IllegalArgumentException if {@code linkEndpointProperties} or its truststore is
     *                                  null.
     */
    public Properties createEncryptionProperties(LinkEndpointProperties linkEndpointProperties) {
        if (linkEndpointProperties == null) {
            throw new IllegalArgumentException("Link endpoint properties are not set");
        }

        var trustStore = linkEndpointProperties.getTruststore();

        if (trustStore == null) {
            throw new IllegalArgumentException("Trust store must not be null");
        }

        var properties = new Properties();

        properties.setProperty(PROVIDER, "org.apache.wss4j.common.crypto.Merlin");

        properties.setProperty(TS_TYPE, trustStore.getType().name());
        properties.setProperty(TS_FILE, trustStore.getPath());
        properties.setProperty(TS_PASSWORD, trustStore.getPassword());
        properties.setProperty(LOAD_CACERTS, "true");

        return properties;
    }
}
