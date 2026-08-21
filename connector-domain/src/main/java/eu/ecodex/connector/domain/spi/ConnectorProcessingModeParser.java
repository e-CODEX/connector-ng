/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi;

import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import java.util.Set;

/**
 * The ProcessingModeParser interface provides functionality to parse a processing mode definition
 * from a given byte array representation. Implementations of this interface should define the
 * specific parsing logic required to interpret the content into the ParsedProcessingMode
 * structure.
 */
public interface ConnectorProcessingModeParser {
    ParsedProcessingMode parse(byte[] content);

    /**
     * Represents the parsed processing mode derived from a processing mode definition.
     *
     * <p>This record is used to encapsulate the details of a processing mode, including the home
     * party name, the associated set of connector parties, services, and actions. It ensures
     * immutability for all collections provided during initialization.
     *
     * @param homePartyName The name of the home party associated with the processing mode. This
     *                      typically identifies the entity that owns or controls the processing
     *                      context.
     * @param parties       The set of {@link ConnectorParty} instances associated with the
     *                      processing mode. Each party represents a distinct entity with a defined
     *                      role.
     * @param services      The set of {@link ConnectorService} instances specified in the
     *                      processing mode. Each service defines a functionality provided within
     *                      the context of the connector.
     * @param actions       The set of {@link ConnectorAction} instances included in the processing
     *                      mode. Each action represents a defined task or operation relevant to
     *                      message exchange.
     */
    record ParsedProcessingMode(
        String homePartyName,
        Set<ConnectorParty> parties,
        Set<ConnectorService> services,
        Set<ConnectorAction> actions
    ) {
        /**
         * Constructs an instance of ParsedProcessingMode while ensuring the immutability of the
         * provided sets.
         */
        public ParsedProcessingMode {
            parties = Set.copyOf(parties);
            services = Set.copyOf(services);
            actions = Set.copyOf(actions);
        }
    }
}
