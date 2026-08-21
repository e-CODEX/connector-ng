/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.token.validation.technical;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.infrastructure.outbound.security.exception.ConnectorTokenException;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenAuthenticationData;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenValidation;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenVerificationData;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.signature.ConnectorTokenTechnicalValidationResult;
import eu.europa.esig.dss.model.DSSDocument;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Technical validation generator for authentication-based (AES) tokens.
 */
@Slf4j
public class ConnectorTokenAuthBasedTechnicalValidationGenerator implements
    ConnectorTokenTechnicalValidationGenerator {
    private final String identityProvider;
    private final ConnectorBusinessMessage message;

    public ConnectorTokenAuthBasedTechnicalValidationGenerator(
        String identityProvider,
        ConnectorBusinessMessage message) {
        this.identityProvider = identityProvider;
        this.message = message;
    }

    @Override
    public ConnectorTokenValidation generate(
        DSSDocument businessDocument,
        DSSDocument detachedSignature) throws Exception {

        log.debug(
            "Create businessDocument: [{}] detachedSignature: [{}]",
            businessDocument, detachedSignature
        );
        try {
            return create(message);
        } catch (DatatypeConfigurationException e) {
            throw new ConnectorTokenException(
                "Failed to generate AES token validation", e
            );
        }
    }

    @Override
    public boolean supportsAuthenticationBased() {
        return true;
    }

    private ConnectorTokenValidation create(ConnectorBusinessMessage message)
        throws DatatypeConfigurationException {
        var datatypeFactory = DatatypeFactory.newInstance(); // create once
        var now = datatypeFactory.newXMLGregorianCalendar(new GregorianCalendar());

        var tokenAuthentication = new ConnectorTokenAuthenticationData();
        tokenAuthentication.setIdentityProvider(identityProvider);
        tokenAuthentication.setUsernameSynonym(message.as4Properties().originalSender());
        tokenAuthentication.setTimeOfAuthentication(now);

        var verificationData = new ConnectorTokenVerificationData();
        verificationData.setAuthenticationData(tokenAuthentication);

        var technicalResult = new ConnectorTokenTechnicalValidationResult();
        technicalResult.setTrustLevel(ConnectorTokenTechnicalTrustLevel.SUCCESSFUL);
        technicalResult.setComment("The authentication is valid.");

        var tokenValidation = new ConnectorTokenValidation();
        tokenValidation.setTechnicalResult(technicalResult);
        tokenValidation.setVerificationData(verificationData);
        tokenValidation.setVerificationTime(now);

        log.debug(
            "AES trust decision: [{}] — {}",
            technicalResult.getTrustLevel(), technicalResult.getComment()
        );

        return tokenValidation;
    }
}
