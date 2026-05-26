package eu.ecodex.connector.infrastructure.property.routing;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents properties defining backend routing rules in the connector configuration.
 *
 * <p>This class is used to define individual backend routing rules, specifying the
 * link partner name and its associated match clause. These properties are used in the broader
 * context of {@link ConnectorMessageRoutingProperties} to configure message routing for the
 * connector.
 */
@Getter
@Setter
public class BackendRuleProperties {
    private String linkName;
    private String matchClause;
}
