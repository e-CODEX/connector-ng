/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property.dss;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for a group of DSS trust list sources.
 */
@Getter
@Setter
public class DssTrustListSourceGroupProperties {
    /**
     * List of LOTL (List of Trusted Lists) source entries.
     */
    private List<DssLotlSourceProperties> lotlSources = new ArrayList<>();
    /**
     * List of TL (Trusted List) source entries.
     */
    private List<DssTlSourceProperties> tlSources = new ArrayList<>();
}
