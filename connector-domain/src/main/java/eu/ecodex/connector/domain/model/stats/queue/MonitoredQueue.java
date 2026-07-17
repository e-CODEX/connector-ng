/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.stats.queue;

/**
 * Represents a monitored queue within the system. This record encapsulates the name and description
 * of a queue to be tracked for operational insights or statistical purposes.
 *
 * @param name        The unique name of the queue being monitored.
 * @param description A description of the queue, detailing its purpose or functionality.
 */
public record MonitoredQueue(String name, String description) {
}
