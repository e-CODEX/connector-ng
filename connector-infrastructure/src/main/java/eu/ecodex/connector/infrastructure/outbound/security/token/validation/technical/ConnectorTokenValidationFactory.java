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

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.content.ConnectorBusinessDocumentAESType;
import eu.ecodex.connector.infrastructure.outbound.security.exception.ConnectorTokenException;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenAESType;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenIssuer;
import eu.ecodex.connector.infrastructure.property.businessdocument.ConnectorBusinessDocumentProperties;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Factory responsible for creating technical validation generators and resolving token issuer
 * metadata.
 *
 * <p>This class encapsulates the logic for:
 * <ul>
 *     <li>Selecting the appropriate {@link ConnectorTokenTechnicalValidationGenerator}
 *         based on the Advanced Electronic System (AES) type</li>
 *     <li>Resolving the AES type from message content or configuration</li>
 *     <li>Constructing the {@link ConnectorTokenIssuer}</li>
 * </ul>
 *
 * <p>The AES type resolution follows this precedence:
 * <ol>
 *     <li>Value provided in the {@link ConnectorMessage}</li>
 *     <li>Default value from {@link ConnectorBusinessDocumentProperties}</li>
 * </ol>
 *
 * <p>The resolved AES type is validated against a configured allowlist.
 */
@Slf4j
@Component
public class ConnectorTokenValidationFactory {
    private final ConnectorTokenTechnicalValidationGenerator signatureBasedTechnicalValidation;
    private final ConnectorBusinessDocumentProperties businessDocumentProperties;
    private final Function<ConnectorMessage, ConnectorTokenTechnicalValidationGenerator>
        authValidationGeneratorFactory;

    /**
     * Constructs a new validation factory.
     *
     * @param signatureBasedTechnicalValidation default generator used for signature-based
     *                                          validation
     * @param businessDocumentProperties        configuration properties
     */
    public ConnectorTokenValidationFactory(
        @Qualifier("connectorTokenSignatureBasedTechnicalValidationGenerator")
        ConnectorTokenTechnicalValidationGenerator signatureBasedTechnicalValidation,
        ConnectorBusinessDocumentProperties businessDocumentProperties) {
        this.signatureBasedTechnicalValidation = signatureBasedTechnicalValidation;
        this.businessDocumentProperties = businessDocumentProperties;
        this.authValidationGeneratorFactory = message ->
            new ConnectorTokenAuthBasedTechnicalValidationGenerator(
                businessDocumentProperties.getAuthenticationValidation()
                                          .getIdentityProvider(),
                message
            );
    }

    /**
     * Creates a technical validation generator based on the resolved AES type.
     *
     * @param message the connector message
     *
     * @return a technical validation generator suited to the AES type
     *
     * @throws ConnectorTokenException  if the AES type cannot be resolved
     * @throws IllegalArgumentException if the AES type is not allowed
     */
    public ConnectorTokenTechnicalValidationGenerator createTechnicalValidation(
        @NonNull ConnectorMessage message) {
        return switch (resolveAESType(message)) {
            case SIGNATURE_BASED -> signatureBasedTechnicalValidation;
            case AUTHENTICATION_BASED -> authValidationGeneratorFactory.apply(message);
        };
    }

    /**
     * Builds the {@link ConnectorTokenIssuer} for a given message.
     *
     * <p>The issuer is constructed from configuration and includes:
     * <ul>
     *     <li>Issuing country</li>
     *     <li>Service provider</li>
     *     <li>Resolved AES type</li>
     * </ul>
     *
     * @param message the connector message
     *
     * @return the populated token issuer
     *
     * @throws ConnectorTokenException if the required configuration is missing
     */
    public ConnectorTokenIssuer getTokenIssuer(ConnectorMessage message) {
        var country = businessDocumentProperties.getCountry();
        var serviceProvider = businessDocumentProperties.getServiceProvider();

        if (!StringUtils.hasText(country) || !StringUtils.hasText(serviceProvider)) {
            throw new ConnectorTokenException(
                "Token issuer configuration is incomplete — country and serviceProvider "
                    + "are required"
            );
        }

        var issuer = new ConnectorTokenIssuer();
        issuer.setCountry(country);
        issuer.setServiceProvider(serviceProvider);
        issuer.setAdvancedElectronicSystem(resolveAESType(message));

        log.debug("Using token issuer [{}]", issuer);

        return issuer;
    }

    /**
     * Resolves the AES type for the message, preferring the type declared on the business document
     * and falling back to the configured default. Validates the resolved type against the
     * configured allowlist.
     */
    private ConnectorTokenAESType resolveAESType(@NonNull ConnectorMessage message) {
        var content = message.businessContent();
        ConnectorBusinessDocumentAESType aesType = null;

        if (content != null && content.businessDocument() != null
            && content.businessDocument().aesType() != null) {
            aesType = content.businessDocument().aesType();
            log.debug("Using AES type [{}] from message", aesType);
        }

        if (aesType == null) {
            aesType = businessDocumentProperties.getDefaultAdvancedSystemType();
            log.debug("Using AES type [{}] from configuration (default)", aesType);
        }

        if (aesType == null) {
            throw new ConnectorTokenException(
                "AES type could not be resolved from message or configuration"
            );
        }

        var allowed = businessDocumentProperties.getAllowedAdvancedSystemTypes();

        if (!allowed.contains(aesType)) {
            throw new IllegalArgumentException(String.format(
                "AES type [%s] is not in the configured allowlist [%s]",
                aesType,
                allowed.stream().map(Object::toString).collect(Collectors.joining(", "))
            ));
        }

        return ConnectorTokenAESType.valueOf(aesType.name());
    }
}
