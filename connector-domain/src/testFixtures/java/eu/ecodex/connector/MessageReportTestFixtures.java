/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageReport;
import java.util.List;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class MessageReportTestFixtures {
    public static List<ConnectorMessageReport> createReport() {
        var reEpo = ConnectorMessageReport.builder()
                                          .year(2026)
                                          .month(6)
                                          .party("RE")
                                          .service("EPO")
                                          .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                                          .total(1)
                                          .build();

        var reFp = ConnectorMessageReport.builder()
                                         .year(2026)
                                         .month(5)
                                         .party("RE")
                                         .service("FP")
                                         .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                                         .total(1)
                                         .build();

        return List.of(reEpo, reFp);
    }
}
