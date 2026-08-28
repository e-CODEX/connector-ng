/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.container.checks.businesscontent;

import eu.ecodex.connector.infrastructure.outbound.security.container.checks.ConnectorMessageContainerChecker;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainer;
import eu.ecodex.connector.infrastructure.outbound.security.util.DigestUtil;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.spi.DSSUtils;
import java.util.Arrays;
import lombok.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Checker for the business content digest.
 */
@Order(4)
@Component("ConnectorContainerBusinessContentDigestChecker")
public class ConnectorContainerBusinessContentDigestChecker implements
    ConnectorMessageContainerChecker {
    @Override
    public void check(@NonNull ConnectorContainer container) {
        var token = container.token();

        if (token == null || token.getDocument() == null) {
            throw new IllegalStateException("The container token has no document");
        }

        var tokenDocument = token.getDocument();

        var digestMethod = tokenDocument.getDigestMethod();

        if (digestMethod == null || digestMethod.getAlgorithm() == null) {
            throw new IllegalStateException("The token does not declare a digest algorithm");
        }

        var digestAlgorithm =
            DigestAlgorithm.forName(digestMethod.getAlgorithm().replace("-", ""));

        var businessContentDocument = container.businessContent().getDocument();

        var computedDigest = DigestUtil.digest(
            DSSUtils.toByteArray(businessContentDocument),
            digestAlgorithm.toString()
        );

        var tokenDigest = tokenDocument.getDigestValue();

        if (!Arrays.equals(computedDigest, tokenDigest)) {
            throw new IllegalStateException(
                "The business content digest does not match the token digest"
            );
        }
    }
}
