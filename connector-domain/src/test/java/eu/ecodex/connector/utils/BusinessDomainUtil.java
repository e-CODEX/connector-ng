package eu.ecodex.connector.utils;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class BusinessDomainUtil {
    public static ConnectorBusinessDomain createDefaultBusinessDomain() {
        var builder = builder();
        return builder.build();
    }

    // is considered as an outgoing message
    private static ConnectorBusinessDomain.ConnectorBusinessDomainBuilder builder() {
        return ConnectorBusinessDomain
                .builder()
                .identifier(BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier())
                .description("test business domain")
                .enabled(true)
                .source(ConnectorConfigurationSource.IMPLEMENTATION)
                .properties(null);
    }
}
