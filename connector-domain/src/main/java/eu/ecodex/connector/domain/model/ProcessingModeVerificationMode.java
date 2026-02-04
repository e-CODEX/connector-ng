/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model;

/**
 * Represents the verification modes for PMode configurations.
 *
 * <p>This enum provides three levels of verification:
 * <ul>
 *     <li>
 *         CREATE: Represents a mode where the PMode is created without strict validation rules.
 *     </li>
 *     <li>
 *         RELAXED: Represents a mode with moderate validation rules, offering a balance between
 *         flexibility and strictness.
 *     </li>
 *     <li>
 *         STRICT: Represents a mode with the highest level of validation, enforcing strict
 *         compliance with defined rules.
 *     </li>
 * </ul>
 */
public enum ProcessingModeVerificationMode {
    CREATE, RELAXED, STRICT
}
