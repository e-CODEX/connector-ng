/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.propertiesprovider.routing;

import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.domain.routing.ConnectorRoutingRulePattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a routing rule used in configuring and evaluating routing logic within a system. A
 * routing rule associates specific link configurations with a set of match criteria and a
 * priority.
 *
 * <p>The {@code ConnectorMessageRoutingRule} class provides functionalities for:
 * <ul>
 *     <li> Defining match criteria using the {@link ConnectorRoutingRulePattern}.
 *     <li> Associating a rule with a specific link name.
 *     <li> Setting a configurable priority, where higher numerical values indicate a higher
 *     priority.
 *     <li> Marking a rule as deleted or active.
 *     <li> Automatically generating unique identifiers for each rule.
 *     <li> Comparing rules based on priority for sorting purposes.
 * </ul>
 *
 * <p>Priority constants:
 * <ul>
 *     <li> {@code HIGH_PRIORITY} = -2000
 *     <li> {@code LOW_PRIORITY} = 2000
 * </ul>
 *
 * <p>Rules with the same priority are considered equal in terms of sorting.
 */
@Data
@Builder
public class ConnectorMessageRoutingRule {
    public static final int HIGH_PRIORITY = -2000;
    public static final int LOW_PRIORITY = 2000;

    private final ConnectorConfigurationSource configurationSource =
        ConnectorConfigurationSource.ENVIRONMENT;

    @NotBlank
    private String linkName;
    @NotNull
    private ConnectorRoutingRulePattern matchClause;
    private String description;
    /**
     * higher numbers mean higher priority.
     */
    @Builder.Default
    private int priority = 0;
    @Builder.Default
    private boolean deleted = false;
    @Builder.Default
    private String routingRuleId = generateID();

    public static String generateID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // sorts by ascending priority, means 0 comes before -2000.
    public static Comparator<ConnectorMessageRoutingRule> getComparator() {
        return (rules1, rule2) -> Integer.compare(rule2.priority, rules1.priority);
    }
}
