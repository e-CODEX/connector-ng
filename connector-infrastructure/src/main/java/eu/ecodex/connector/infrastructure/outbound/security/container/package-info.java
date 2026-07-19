/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/**
 * Provides security infrastructure components for creating ASIC-S containers.
 *
 * <p>This package contains the infrastructure-level services and definitions used to build secured
 * connector containers for outbound exchanges. These components validate the business content,
 * generate the required trust token artefacts, and assemble the final signed ASIC-S container that
 * can be used by higher-level connector workflows.
 *
 * <p>The package is focused on container creation concerns, including:
 * <ul>
 *   <li>preparing business content for inclusion in the container;
 *   <li>generating XML and PDF trust token representations;
 *   <li>defining container file names and structure;
 *   <li>creating and signing the final ASIC-S document.
 * </ul>
 */
package eu.ecodex.connector.infrastructure.outbound.security.container;
