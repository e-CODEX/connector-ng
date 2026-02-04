/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.util;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import lombok.experimental.UtilityClass;

/**
 * Utility class providing functionality for managing the current business domain uuid within the
 * connector system.
 *
 * <p>This class is designed to operate as a thread-local storage mechanism, ensuring that
 * business domain context is preserved on a per-thread basis. It allows for setting and retrieving
 * the business domain uuid associated with the current execution context.
 *
 * <p>It is particularly useful in scenarios where operations are tied to a specific business
 * domain and need to maintain this association throughout their lifecycle.
 */
@UtilityClass
public class ConnectorBusinessDomainUtil {
    private static final ThreadLocal<ConnectorBusinessDomainIdentifier> currentMessageLaneIdentifier
            = new ThreadLocal<>();

    /**
     * Updates the current business domain context for the calling thread.
     *
     * <p>This method sets the {@link ConnectorBusinessDomainIdentifier} for the current thread,
     * associating the thread's execution context with the specified business domain. It ensures
     * that later operations within the same thread are aware of and adhere to the specified
     * business domain configuration.
     *
     * @param businessDomainIdentifier The uuid representing the business domain to be associated
     *                                 with the current thread.
     */
    public static void setCurrentBusinessDomain(
            ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        currentMessageLaneIdentifier.set(businessDomainIdentifier);
    }
}
