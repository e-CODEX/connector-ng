/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.token.trustok.pdf;

/**
 * Represents different styling options for titles in a document.
 *
 * <p>This enumeration is used to define various levels of headings and text styles
 * that can be applied to content. Each entry corresponds to a specific style, which may be linked
 * to a particular font in associated configurations or systems.
 */
public enum TitleStyle {
    HEADER1,
    HEADER2,
    HEADER3,
    HEADER4,
    HEADER5,
    DEFAULT,
    CODE
}
