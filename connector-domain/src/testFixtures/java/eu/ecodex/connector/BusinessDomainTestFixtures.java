package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class BusinessDomainTestFixtures {
    public static ConnectorBusinessDomain createDefaultBusinessDomain() {
        var builder = builder();
        return builder.build();
    }

    // is considered as an outgoing message
    private static ConnectorBusinessDomain.ConnectorBusinessDomainBuilder builder() {
        return ConnectorBusinessDomain
                .builder()
                .identifier(
                        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
                )
                .description("test business domain")
                .enabled(true)
                .source(ConnectorConfigurationSource.IMPLEMENTATION);
    }
}
